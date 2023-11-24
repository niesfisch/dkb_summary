package de.marcelsauer.bank.domain.group;

import de.marcelsauer.bank.domain.Transaction;
import java.util.Collection;

public final class GroupByAuftraggeberBeguenstigter implements GroupBy {

  private final Collection<Transaction> transactions;

  public GroupByAuftraggeberBeguenstigter(Collection<Transaction> transactions) {
    this.transactions = transactions;
  }

  @Override
  public GroupedTransactions get() {
    GroupedTransactions groupedTransactions = new GroupedTransactions();
    for (Transaction transaction : transactions) {
      groupedTransactions.addToGroup(Group.of(transaction.auftraggeberBeguenstigter()), transaction);
    }
    return groupedTransactions;
  }
}
