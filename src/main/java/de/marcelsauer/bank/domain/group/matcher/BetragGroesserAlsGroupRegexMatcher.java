package de.marcelsauer.bank.domain.group.matcher;

import de.marcelsauer.bank.domain.Transaction;
import java.math.BigDecimal;

public record BetragGroesserAlsGroupRegexMatcher(BigDecimal betragGrosserAls, String targetGroup) implements GroupRegexMatcher {

  @Override
  public boolean matches(Transaction transaction) {
    return transaction.betrag().compareTo(betragGrosserAls) >= 1;
  }

  @Override
  public String group() {
    return targetGroup;
  }

  @Override
  public String toString() {
    return "BetragGroesserAlsGroupRegexMatcher [betragGrosserAls=" + betragGrosserAls + ", targetGroup=" + targetGroup + "]";
  }
}
