package de.marcelsauer.bank.domain.filter;

import de.marcelsauer.bank.domain.Transaction;
import java.util.Collection;

public interface TransactionFilter {

  boolean matches(Transaction transaction);

  String description();

  Collection<String> fields();

}
