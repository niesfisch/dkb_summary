package de.marcelsauer.bank.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TransactionTest {

  @Test
  void shouldWork() {
    assertThat(getTransaction(new BigDecimal(1)).isGutschrift()).isTrue();
    assertThat(getTransaction(new BigDecimal(999999)).isGutschrift()).isTrue();

    assertThat(getTransaction(new BigDecimal(0)).isGutschrift()).isFalse();
    assertThat(getTransaction(new BigDecimal(-1)).isGutschrift()).isFalse();
    assertThat(getTransaction(new BigDecimal(-99999)).isGutschrift()).isFalse();
  }

  private static Transaction getTransaction(BigDecimal betrag) {
    return new Transaction(
        null,
        null,
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