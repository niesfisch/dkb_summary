package de.marcelsauer.bank.domain.filter;

import de.marcelsauer.bank.domain.Transaction;
import java.util.Collection;
import java.util.List;

public final class FilterTransactionsExclude extends FilterTransactions {

  public FilterTransactionsExclude(Collection<Transaction> transactions, List<TransactionFilter> transactionFilters) {
    super(transactions, transactionFilters);
  }

  @Override
  protected boolean defaultKeepIfNoMatch() {
    return true;
  }
}
