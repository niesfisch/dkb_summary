package de.marcelsauer.bank.domain.group;

import static org.assertj.core.api.Assertions.assertThat;

import de.marcelsauer.bank.domain.Transaction;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class GroupByAuftraggeberBeguenstigterTest {

  @Test
  void shouldGroupBy() {
    // given
    List<Transaction> transactions = List.of(
        tx("auftraggeber1", new BigDecimal(10)),
        tx("auftraggeber1", new BigDecimal(20)),
        tx("auftraggeber1", new BigDecimal(30)),

        tx("auftraggeber123", new BigDecimal(1)),
        tx("auftraggeber123", new BigDecimal(2)),
        tx("auftraggeber123", new BigDecimal(3)),

        tx("auftraggeber2", new BigDecimal(444)),

        tx("auftraggeber11112", new BigDecimal(555))
    );

    // when
    GroupedTransactions groupedBy = new GroupByAuftraggeberBeguenstigter(transactions).get();

    // then
    assertThat(groupedBy.groupSize()).isEqualTo(4);
    assertThat(groupedBy.groupNames()).containsExactlyInAnyOrderElementsOf(
        List.of(
            "auftraggeber1",
            "auftraggeber123",
            "auftraggeber2",
            "auftraggeber11112"
        )
    );
    assertThat(groupedBy.transactionsForGroup(Group.of("auftraggeber1")).size()).isEqualTo(3);
    assertThat(groupedBy.transactionsForGroup(Group.of("auftraggeber123")).size()).isEqualTo(3);
    assertThat(groupedBy.transactionsForGroup(Group.of("auftraggeber2")).size()).isEqualTo(1);
    assertThat(groupedBy.transactionsForGroup(Group.of("auftraggeber11112")).size()).isEqualTo(1);
  }

  private Transaction tx(String auftraggeberBeguenstigter, BigDecimal betrag) {
    return new Transaction(null, null, null, auftraggeberBeguenstigter, null, null, null, betrag, null, null, null);
  }
}
