package de.marcelsauer.bank.domain;

import java.math.BigDecimal;
import java.util.Collection;

public final class EinnahmenAusgaben {

  private final Collection<Transaction> transactions;

  public EinnahmenAusgaben(Collection<Transaction> transactions) {
    this.transactions = transactions;
  }

  public BigDecimal einnahmen() {
    BigDecimal einnahmen = new BigDecimal(0);
    for (Transaction transaction : transactions) {
      if (transaction.isGutschrift()) {
        einnahmen = einnahmen.add(transaction.betrag());
      }
    }
    return einnahmen;
  }

  public BigDecimal ausgaben() {
    BigDecimal ausgaben = new BigDecimal(0);
    for (Transaction transaction : transactions) {
      if (!transaction.isGutschrift()) {
        ausgaben = ausgaben.add(transaction.betrag());
      }
    }
    return ausgaben.multiply(BigDecimal.valueOf(-1)); // TODO + vs -
  }

}
