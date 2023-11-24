package de.marcelsauer.bank;

import de.marcelsauer.bank.adapter.dkb.DkbCsvTransactionRepository;
import de.marcelsauer.bank.config.plain.Config;
import de.marcelsauer.bank.domain.EinnahmenAusgaben;
import de.marcelsauer.bank.domain.Transaction;
import de.marcelsauer.bank.domain.TransactionRepository;
import de.marcelsauer.bank.domain.filter.FilterTransactionsExclude;
import de.marcelsauer.bank.domain.group.Group;
import de.marcelsauer.bank.domain.group.GroupByAuftraggeberBeguenstigter;
import de.marcelsauer.bank.domain.group.GroupByEinnahmenAusgaben;
import de.marcelsauer.bank.domain.group.GroupByMonthAndYear;
import de.marcelsauer.bank.domain.group.GroupByRegexMatcher;
import de.marcelsauer.bank.domain.group.GroupedTransactions;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dkb {

  /*********************************************
   * change to your values
   *********************************************/
  // the file you have downloaded from dkb
  public static final String DKB_INPUT_CSV = "/home/msauer/dev/workspace-private/dkb_resourcen/dkb_2022.csv";
  // config file
  public static final String CONFIG_FILE = "/home/msauer/dev/workspace-private/dkb_resourcen/config.properties";
  // directory where to write the results to
  public static final String OUT_DIR = "/home/msauer/dev/workspace-private/dkb_resourcen/results/";

  /*********************************************
   * do not change anything below unless
   * you know what you are doing
   *********************************************/
  private static final Logger LOG = LoggerFactory.getLogger(Dkb.class);

  private static final String GROUPED_BY_MONTH_CSV_FILE_NAME = "grouped_by_month_year.csv";
  private static final String GROUPED_BY_REGEX_CSV_FILE_NAME = "grouped_by_regex.csv";
  private static final String GROUPED_BY_AUFTRAGGEBER_OR_BEGUESTIGTER = "grouped_by_auftraggeber_or_beguenstigter.csv";
  private static final String EINNAHMEN_CSV_FILE_NAME = "money_received.csv";
  private static final String AUSGABEN_CSV_FILE_NAME = "money_spent.csv";

  private final String dkbInputCsv;
  private final String outDir;
  private final Config config;

  public Dkb(String dkbInputCsv, String outDir, String configFile) {
    this.dkbInputCsv = dkbInputCsv;
    this.outDir = outDir;
    this.config = Config.from(Paths.get(configFile));
  }

  public static void main(String[] args) {
    Dkb dkb = new Dkb(
        DKB_INPUT_CSV,
        OUT_DIR,
        CONFIG_FILE
    );
    dkb.run();
  }

  private void run() {
    Path csvFile = Paths.get(this.dkbInputCsv);

    // this can easily be extended to other banks, just implement the TransactionRepository interface
    TransactionRepository transactionRepository = new DkbCsvTransactionRepository(csvFile);

    // raw transactions, without any filtering
    Collection<Transaction> rawTransactions = transactionRepository.load();

    // filter out stuff we do not need
    Collection<Transaction> withoutExcluded = new FilterTransactionsExclude(rawTransactions, config.excludeFilters).filter();

    saveAuftraggeberOrBeguenstigterToCsvFile(withoutExcluded);
    saveEinnahmenAusgabenToCsvFile(withoutExcluded);
    savePerMonthToCsvFile(withoutExcluded);
    savePerRegexGroupToCsvFile(withoutExcluded);

    LOG.info("All good :) find your results in {}", this.outDir);
  }

  private void savePerRegexGroupToCsvFile(Collection<Transaction> transactions) {
    GroupByRegexMatcher groupByRegexMatcher = new GroupByRegexMatcher(transactions, config.groupRegexMatchers);
    GroupedTransactions grouped = groupByRegexMatcher.get();
    writeToFile(txGroupedByEinnahmenAusgabenToCsv(grouped), outDir + GROUPED_BY_REGEX_CSV_FILE_NAME);
  }

  private void savePerMonthToCsvFile(Collection<Transaction> transactions) {
    GroupedTransactions grouped = new GroupByMonthAndYear(transactions).get();
    writeToFile(txGroupedByEinnahmenAusgabenToCsv(grouped), outDir + GROUPED_BY_MONTH_CSV_FILE_NAME);
  }

  private String txGroupedByEinnahmenAusgabenToCsv(GroupedTransactions grouped) {
    StringBuilder sb = new StringBuilder();
    sb.append("Gruppe;Einnahmen;Ausgaben\n");
    for (Group group : grouped.transactionsByGroup().keySet()) {
      EinnahmenAusgaben summary = new EinnahmenAusgaben(grouped.transactionsByGroup().get(group));
      sb.append("%s;%s;%s%n".formatted(group.name(), summary.einnahmen(), summary.ausgaben()));
    }
    return sb.toString();
  }

  private void saveAuftraggeberOrBeguenstigterToCsvFile(Collection<Transaction> transactions) {
    GroupedTransactions groupedByEinnahmenAusgaben = new GroupByAuftraggeberBeguenstigter(transactions).get();
    String groupedByEinnahmenAusgabenPerBeguenstigerOrAuftraggeber = txGroupedByEinnahmenAusgabenToCsv(groupedByEinnahmenAusgaben);
    writeToFile(groupedByEinnahmenAusgabenPerBeguenstigerOrAuftraggeber, outDir + GROUPED_BY_AUFTRAGGEBER_OR_BEGUESTIGTER);
  }

  private void saveEinnahmenAusgabenToCsvFile(Collection<Transaction> transactions) {
    GroupedTransactions groupedByEinnahmenAusgaben = new GroupByEinnahmenAusgaben(transactions).get();

    String einnahmenCsv = toRawCsv(groupedByEinnahmenAusgaben, "Einnahmen");
    String ausgabenCsv = toRawCsv(groupedByEinnahmenAusgaben, "Ausgaben");

    writeToFile(einnahmenCsv, outDir + EINNAHMEN_CSV_FILE_NAME);
    writeToFile(ausgabenCsv, outDir + AUSGABEN_CSV_FILE_NAME);
  }

  private String toRawCsv(GroupedTransactions groupedByEinnahmenAusgaben, String einnahmenOrAusgaben) {
    StringBuilder result = new StringBuilder();
    result.append("\"Buchungstag\";\"Wertstellung\";\"Buchungstext\";\"Auftraggeber / Begünstigter\";\"Verwendungszweck\";\"Kontonummer\";\"BLZ\";\"Betrag (EUR)\";\"Gläubiger-ID\";\"Mandatsreferenz\";\"Kundenreferenz\";\n");
    for (Transaction transaction : groupedByEinnahmenAusgaben.transactionsForGroup(Group.of(einnahmenOrAusgaben))) {
      result.append(transaction.toCsvLine()); // todo csv format
    }
    return result.toString();
  }

  private void writeToFile(String csvToPrint, String file) {
    Path path = Paths.get(file);
    try {
      Files.writeString(path, csvToPrint, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

}
