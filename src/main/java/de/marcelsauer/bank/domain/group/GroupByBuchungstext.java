package de.marcelsauer.bank.domain.group;

import de.marcelsauer.bank.domain.Transaction;
import java.util.Collection;

public final class GroupByBuchungstext implements GroupBy {

  private final Collection<Transaction> transactions;

  public GroupByBuchungstext(Collection<Transaction> transactions) {
    this.transactions = transactions;
  }

  @Override
  public GroupedTransactions get() {
    GroupedTransactions groupedTransactions = new GroupedTransactions();
    for (Transaction transaction : transactions) {
      groupedTransactions.addToGroup(Group.of(transaction.buchungstext()), transaction);
    }
    return groupedTransactions;
  }
}
