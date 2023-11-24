package de.marcelsauer.bank.domain.group;

import static org.assertj.core.api.Assertions.assertThat;

import de.marcelsauer.bank.domain.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class GroupByMonthAndYearTest {

  @Test
  void shouldGroupByMonthAndYearForEmpty() {
    // given
    Collection<Transaction> transactions = Collections.emptyList();
    GroupByMonthAndYear groupBy = new GroupByMonthAndYear(transactions);

    // when
    GroupedTransactions grouped = groupBy.get();

    // then
    assertThat(grouped.groupSize()).isEqualTo(0);
  }

  @Test
  void shouldGroupByMonthAndYear() {
    // given
    Collection<Transaction> transactions = List.of(
        getTransaction(LocalDate.of(2021, 1, 1), new BigDecimal(100)),
        getTransaction(LocalDate.of(2021, 1, 1), new BigDecimal(100)),
        getTransaction(LocalDate.of(2021, 1, 1), new BigDecimal(100)),
        getTransaction(LocalDate.of(2021, 1, 1), new BigDecimal(100)),
        getTransaction(LocalDate.of(2021, 2, 1), new BigDecimal(100)),
        getTransaction(LocalDate.of(2021, 3, 1), new BigDecimal(100)),
        getTransaction(LocalDate.of(2021, 4, 1), new BigDecimal(100)),
        getTransaction(LocalDate.of(2021, 5, 1), new BigDecimal(100))

    );
    GroupByMonthAndYear groupBy = new GroupByMonthAndYear(transactions);

    // when
    GroupedTransactions grouped = groupBy.get();

    // then
    assertThat(grouped.groupSize()).isEqualTo(5);
    assertThat(grouped.groupNames()).containsExactlyInAnyOrder("Apr 2021", "Feb 2021", "Mai 2021", "März 2021", "Jan 2021");
    assertThat(grouped.transactionsForGroup(Group.of("Jan 2021"))).hasSize(4);
    assertThat(grouped.transactionsForGroup(Group.of("Feb 2021"))).hasSize(1);
    assertThat(grouped.transactionsForGroup(Group.of("März 2021"))).hasSize(1);
    assertThat(grouped.transactionsForGroup(Group.of("Apr 2021"))).hasSize(1);
    assertThat(grouped.transactionsForGroup(Group.of("Mai 2021"))).hasSize(1);
  }

  private static Transaction getTransaction(LocalDate buchungstag, BigDecimal betrag) {
    return new Transaction(
        null,
        buchungstag,
        null,
        null,
        null,
        null,
        null,
        betrag,
        null,
        null,
        null
    );
  }
}