package de.marcelsauer.bank.domain.group;

import static org.assertj.core.api.Assertions.assertThat;

import de.marcelsauer.bank.domain.Transaction;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class GroupByEinnahmenAusgabenTest {

  @Test
  void shouldGroupByEinnahmenAusgaben() {
    // given
    List<Transaction> transactions = List.of(
        tx(new BigDecimal("1")),
        tx(new BigDecimal("2")),
        tx(new BigDecimal("3")),
        tx(new BigDecimal("-4")),
        tx(new BigDecimal("-5"))
    );

    // when
    GroupedTransactions groupedByEinnahmenAusgaben = new GroupByEinnahmenAusgaben(transactions).get();

    // then
    assertThat(groupedByEinnahmenAusgaben.groupNames()).containsExactlyInAnyOrderElementsOf(List.of("Einnahmen", "Ausgaben"));
    assertThat(groupedByEinnahmenAusgaben.groupSize()).isEqualTo(2);
    assertThat(groupedByEinnahmenAusgaben.transactionsForGroup(Group.of("Einnahmen")).size()).isEqualTo(3);
    assertThat(groupedByEinnahmenAusgaben.transactionsForGroup(Group.of("Ausgaben")).size()).isEqualTo(2);
  }

  private Transaction tx(BigDecimal betrag) {
    return new Transaction(null, null, null, null, null, null, null, betrag, null, null, null);
  }
}