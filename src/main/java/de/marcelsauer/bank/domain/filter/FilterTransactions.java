package de.marcelsauer.bank.domain.filter;

import de.marcelsauer.bank.domain.Transaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FilterTransactions {

  private static final Logger LOG = LoggerFactory.getLogger(FilterTransactionsExclude.class);
  private final Collection<Transaction> transactions;
  private final List<TransactionFilter> transactionFilters;

  public FilterTransactions(Collection<Transaction> transactions, List<TransactionFilter> transactionFilters) {
    if (transactionFilters.isEmpty()) {
      throw new IllegalArgumentException("Please provide at least one filter");
    }
    this.transactions = transactions;
    this.transactionFilters = transactionFilters;
  }

  protected abstract boolean defaultKeepIfNoMatch();

  public Collection<Transaction> filter() {
    Collection<Transaction> result = new ArrayList<>();
    for (Transaction transaction : transactions) {
      boolean includeInResult = defaultKeepIfNoMatch(); // false, only include
      Collection<TransactionFilter> filteringFilters = new HashSet<>();
      for (TransactionFilter transactionFilter : transactionFilters) {
        if (transactionFilter.matches(transaction)) {
          includeInResult = !defaultKeepIfNoMatch();
          if (!includeInResult) {
            filteringFilters.add(transactionFilter);
          }
        }
      }
      if (includeInResult) {
        result.add(transaction);
      } else {
        Collection<String> desc = filteringFilters.stream().map(TransactionFilter::description).collect(Collectors.toSet());
        Collection<String> fields = filteringFilters.stream().map(TransactionFilter::fields).flatMap(Collection::stream).collect(Collectors.toSet());
        LOG.debug("filter {} for fields {} excluded transaction {}", desc, fields, transaction);
      }
    }
    return result;
  }


}
