package de.marcelsauer.bank.domain;

import java.util.Collection;

public interface TransactionRepository {

  /**
   * load transactions. duplicate lines (e.g. by merging multiple csv files) will be filtered out
   */
  Collection<Transaction> load();
}
