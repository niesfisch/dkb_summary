package de.marcelsauer.bank.domain.filter.filter;

import de.marcelsauer.bank.domain.Transaction;
import de.marcelsauer.bank.domain.filter.TransactionFilter;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

public final class AuftraggeberOderBeguenstigterTransactionFilter implements TransactionFilter {

  private final Pattern pattern;

  public AuftraggeberOderBeguenstigterTransactionFilter(String regex) {
    this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
  }

  @Override
  public boolean matches(Transaction transaction) {
    return pattern.matcher(transaction.auftraggeberBeguenstigter()).find();
  }

  @Override
  public String description() {
    return "%s regex: %s".formatted(this.getClass().getSimpleName(), pattern.toString());
  }

  @Override
  public Collection<String> fields() {
    return Set.of("auftraggeberBeguenstigter");
  }

  @Override
  public String toString() {
    return "AuftraggeberOderBeguenstigterTransactionFilter [pattern=" + pattern + "]";
  }
}
