package de.marcelsauer.bank.domain.group.matcher;

import de.marcelsauer.bank.domain.Transaction;

public interface GroupRegexMatcher {

  boolean matches(Transaction transaction);

  String group();
}
