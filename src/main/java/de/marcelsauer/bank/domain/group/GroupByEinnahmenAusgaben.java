package de.marcelsauer.bank.domain.group;

import de.marcelsauer.bank.domain.Transaction;
import java.util.Collection;

public final class GroupByEinnahmenAusgaben implements GroupBy {

  private final Collection<Transaction> transactions;

  public GroupByEinnahmenAusgaben(Collection<Transaction> transactions) {
    this.transactions = transactions;
  }

  @Override
  public GroupedTransactions get() {
    GroupedTransactions groupedTransactions = new GroupedTransactions();
    for (Transaction transaction : transactions) {
      if (transaction.isGutschrift()) {
        groupedTransactions.addToGroup(Group.of("Einnahmen"), transaction);
      } else {
        groupedTransactions.addToGroup(Group.of("Ausgaben"), transaction);
      }
    }
    return groupedTransactions;
  }
}
