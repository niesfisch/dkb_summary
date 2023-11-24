package de.marcelsauer.bank.domain.filter.filter;

import de.marcelsauer.bank.domain.Transaction;
import de.marcelsauer.bank.domain.filter.TransactionFilter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

public record BetragGroesserAlsTransactionFilter(BigDecimal betragGrosserAls) implements TransactionFilter {

  @Override
  public boolean matches(Transaction transaction) {
    return transaction.betrag().compareTo(betragGrosserAls) >= 1;
  }

  @Override
  public String description() {
    return "%s betrag > %s".formatted(this.getClass().getSimpleName(), betragGrosserAls.toString());
  }

  @Override
  public Collection<String> fields() {
    return Set.of("betrag");
  }

  @Override
  public String toString() {
    return "BetragGroesserAlsTransactionFilter [betragGrosserAls=" + betragGrosserAls + "]";
  }
}
