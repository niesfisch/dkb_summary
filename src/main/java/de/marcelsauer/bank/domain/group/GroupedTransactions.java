package de.marcelsauer.bank.domain.group;

import de.marcelsauer.bank.domain.Transaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupedTransactions {

  private final Map<Group, Collection<Transaction>> transactionsByGroup = new LinkedHashMap<>(); // keep order

  public int groupSize() {
    return transactionsByGroup.size();
  }

  public Map<Group, Collection<Transaction>> transactionsByGroup() {
    return Collections.unmodifiableMap(transactionsByGroup);
  }

  public Collection<String> groupNames() {
    return transactionsByGroup.keySet().stream().map(Group::name).collect(Collectors.toSet());
  }

  public Collection<Transaction> transactionsForGroup(Group group) {
    if (!transactionsByGroup.containsKey(group)) {
      throw new IllegalArgumentException("could not find entries for group '%s'".formatted(group));
    }
    return transactionsByGroup.get(group);
  }

  void addToGroup(Group group, Transaction transaction) {
    Collection<Transaction> tmp = transactionsByGroup.getOrDefault(group, new ArrayList<>());
    tmp.add(transaction);
    transactionsByGroup.put(group, tmp);
  }

  void addToGroup(Group group, Collection<Transaction> transactions) {
    Collection<Transaction> tmp = transactionsByGroup.getOrDefault(group, new ArrayList<>());
    tmp.addAll(transactions);
    transactionsByGroup.put(group, tmp);
  }
}
