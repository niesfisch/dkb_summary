package de.marcelsauer.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class EinnahmenAusgabenTest {

  @Test
  void shouldCalculateEinnahmeAusgaben() {
    // given
    Transaction a = tx(BigDecimal.ZERO);
    Transaction b = tx(new BigDecimal("1200"));
    Transaction c = tx(new BigDecimal("-200"));
    Transaction d = tx(new BigDecimal("-200"));
    Collection<Transaction> transactions = List.of(a, b, c, d);
    EinnahmenAusgaben einnahmenAusgaben = new EinnahmenAusgaben(transactions);

    // when
    BigDecimal ausgaben = einnahmenAusgaben.ausgaben();
    BigDecimal einnahmen = einnahmenAusgaben.einnahmen();

    // then
    assertThat(ausgaben).isEqualTo(new BigDecimal("400"));
    assertThat(einnahmen).isEqualTo(new BigDecimal("1200"));
  }

  private Transaction tx(BigDecimal betrag) {
    return new Transaction(null, null, null, null, null, null, null, betrag, null, null, null);
  }
}