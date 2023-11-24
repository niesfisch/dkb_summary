package de.marcelsauer.bank.domain.filter.filter;

import de.marcelsauer.bank.domain.Transaction;
import de.marcelsauer.bank.domain.filter.TransactionFilter;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

public final class VerwendungszweckTransactionFilter implements TransactionFilter {

  private final Pattern pattern;

  public VerwendungszweckTransactionFilter(String regex) {
    this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  }

  @Override
  public boolean matches(Transaction transaction) {
    return pattern.matcher(transaction.verwendungszweck()).find();
  }

  @Override
  public String description() {
    return "%s regex: %s".formatted(this.getClass().getSimpleName(), pattern.toString());
  }

  @Override
  public Collection<String> fields() {
    return Set.of("verwendungszweck");
  }

  public String regex() {
    return pattern.toString();
  }

  @Override
  public String toString() {
    return "VerwendungszweckTransactionFilter [pattern=" + pattern + "]";
  }
}
