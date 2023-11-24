package de.marcelsauer.bank.adapter.dkb;

import static org.assertj.core.api.Assertions.assertThat;

import de.marcelsauer.bank.domain.Transaction;
import de.marcelsauer.bank.domain.TransactionRepository;
import de.marcelsauer.bank.domain.filter.FilterTransactionsExclude;
import de.marcelsauer.bank.domain.filter.FilterTransactionsInclude;
import de.marcelsauer.bank.domain.filter.filter.AuftraggeberOderBeguenstigterTransactionFilter;
import de.marcelsauer.bank.domain.filter.filter.BetragGroesserAlsTransactionFilter;
import de.marcelsauer.bank.domain.filter.filter.BuchungstextTransactionFilter;
import de.marcelsauer.bank.domain.group.Group;
import de.marcelsauer.bank.domain.group.GroupByMonthAndYear;
import de.marcelsauer.bank.domain.group.GroupByRegexMatcher;
import de.marcelsauer.bank.domain.group.GroupedTransactions;
import de.marcelsauer.bank.domain.group.matcher.AuftraggeberOderBeguenstigterGroupRegexMatcher;
import de.marcelsauer.bank.domain.group.matcher.BetragGroesserAlsGroupRegexMatcher;
import de.marcelsauer.bank.domain.group.matcher.BuchungstextGroupRegexMatcher;
import de.marcelsauer.bank.domain.group.matcher.VerwendungszweckGroupRegexMatcher;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class DkbScenarioTest {

  @Test
  void shouldPrintSummaryForGroup() throws URISyntaxException {

    Path csvFile = Paths.get(Objects.requireNonNull(getClass().getResource("/dkb_test.csv")).toURI());
    TransactionRepository transactionRepository = new DkbCsvTransactionRepository(csvFile);

    // raw tx
    Collection<Transaction> rawTransactions = transactionRepository.load();
    assertThat(rawTransactions.size()).isEqualTo(30);

    // filter out stuff we do not need
    Collection<Transaction> withoutExcluded = new FilterTransactionsExclude(rawTransactions, List.of(
        new BuchungstextTransactionFilter("Abschluss"),
        new AuftraggeberOderBeguenstigterTransactionFilter(".*Vodafon.*"),
        new BetragGroesserAlsTransactionFilter(new BigDecimal(4800))
    )).filter();
    assertThat(withoutExcluded.size()).isEqualTo(25);

    // only include stuff we want, just for test purposes
    Collection<Transaction> onlyIncluded = new FilterTransactionsInclude(withoutExcluded, List.of(
        new BuchungstextTransactionFilter(".*Gehalt.*"),
        new AuftraggeberOderBeguenstigterTransactionFilter(".*Budni.*"),
        new AuftraggeberOderBeguenstigterTransactionFilter(".*Diba.*"),
        new AuftraggeberOderBeguenstigterTransactionFilter(".*Rewe.*"), // we skip edeka and denns
        new AuftraggeberOderBeguenstigterTransactionFilter(".*VITA.*")
    )).filter();
    assertThat(onlyIncluded.size()).isEqualTo(21);

    // group
    GroupByRegexMatcher groupByRegexMatcher = new GroupByRegexMatcher(
        onlyIncluded,
        List.of(
            // betrag > x
            new BetragGroesserAlsGroupRegexMatcher(BigDecimal.valueOf(4000), "Grossbetrag"),

            // buchungstext
            new BuchungstextGroupRegexMatcher(".*Gehalt.*", "Gehalt"),

            // verwendungszweck
            new VerwendungszweckGroupRegexMatcher(".*Sparplan.*", "Sparen"),

            // auftrag oder beguenstigter
            new AuftraggeberOderBeguenstigterGroupRegexMatcher(".*Budni.*", "Budni"),
            new AuftraggeberOderBeguenstigterGroupRegexMatcher(".*Rewe.*", "Rewe"),
            new AuftraggeberOderBeguenstigterGroupRegexMatcher(".*", "Andere") // e-vita gas
        )
    );

    GroupedTransactions groupedTransactions = groupByRegexMatcher.get();
    assertThat(groupedTransactions.groupSize()).isEqualTo(6);
    List<String> expectedGroups = List.of(
        "Grossbetrag",
        "Gehalt",
        "Sparen",
        "Budni",
        "Rewe",
        "Andere"
    );
    assertThat(groupedTransactions.groupNames()).containsAll(expectedGroups);
    assertThat(groupedTransactions.transactionsForGroup(Group.of("Grossbetrag")).size()).isEqualTo(1);
    assertThat(groupedTransactions.transactionsForGroup(Group.of("Gehalt")).size()).isEqualTo(12); // one sorted into grossbetrag
    assertThat(groupedTransactions.transactionsForGroup(Group.of("Sparen")).size()).isEqualTo(2);
    assertThat(groupedTransactions.transactionsForGroup(Group.of("Budni")).size()).isEqualTo(2);
    assertThat(groupedTransactions.transactionsForGroup(Group.of("Rewe")).size()).isEqualTo(2);
    assertThat(groupedTransactions.transactionsForGroup(Group.of("Andere")).size()).isEqualTo(2);
  }

  @Test
  void shouldPrintSummaryGroupedByMonth() throws URISyntaxException {

    Path csvFile = Paths.get(Objects.requireNonNull(getClass().getResource("/dkb_test.csv")).toURI());
    TransactionRepository transactionRepository = new DkbCsvTransactionRepository(csvFile);

    // raw tx
    Collection<Transaction> rawTransactions = transactionRepository.load();
    assertThat(rawTransactions.size()).isEqualTo(30);

    // filter out stuff we do not need
    Collection<Transaction> withoutExcluded = new FilterTransactionsExclude(rawTransactions, List.of(
        new BuchungstextTransactionFilter("Abschluss")
    )).filter();
    assertThat(withoutExcluded.size()).isEqualTo(28);

    // group
    GroupedTransactions groupedByMonth = new GroupByMonthAndYear(withoutExcluded).get();
    assertThat(groupedByMonth.groupSize()).isEqualTo(13);
    Map<Group, Integer> expectedTransactionSizePerGroup = new HashMap<>();
    expectedTransactionSizePerGroup.put(Group.of("Jan 2021"), 2);
    expectedTransactionSizePerGroup.put(Group.of("Feb 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("März 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Apr 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Mai 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Juni 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Juli 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Aug 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Sept 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Okt 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Nov 2021"), 1);
    expectedTransactionSizePerGroup.put(Group.of("Dez 2021"), 8);
    expectedTransactionSizePerGroup.put(Group.of("Dez 2022"), 8);

    assertThat(groupedByMonth.groupNames()).containsExactlyInAnyOrderElementsOf(expectedTransactionSizePerGroup.keySet().stream().map(Group::name).collect(Collectors.toSet()));
    for (Entry<Group, Integer> entry : expectedTransactionSizePerGroup.entrySet()) {
      assertThat(groupedByMonth.transactionsForGroup(entry.getKey()).size()).isEqualTo(entry.getValue());
    }
  }


  @Test
  void shouldHandleDuplicateTx() throws URISyntaxException {
    Path csvFile = Paths.get(Objects.requireNonNull(getClass().getResource("/dkb_test.csv")).toURI());
    TransactionRepository transactionRepository = new DkbCsvTransactionRepository(csvFile);

    // raw tx
    Collection<Transaction> rawTransactions = transactionRepository.load();
    assertThat(rawTransactions.size()).isEqualTo(30);
  }
}
