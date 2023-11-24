package de.marcelsauer.bank.domain.group;

import de.marcelsauer.bank.domain.Transaction;
import de.marcelsauer.bank.domain.group.matcher.GroupRegexMatcher;
import java.util.Collection;

/**
 * groups transactions by a regex matcher
 * e.g. '.*' will match all transactions
 */
public final class GroupByRegexMatcher implements GroupBy {

  private final Collection<Transaction> transactions;
  private final Collection<GroupRegexMatcher> groupRegexMatchers;

  public GroupByRegexMatcher(Collection<Transaction> transactions, Collection<GroupRegexMatcher> groupRegexMatchers) {
    this.transactions = transactions;
    this.groupRegexMatchers = groupRegexMatchers;
  }

  @Override
  public GroupedTransactions get() {
    GroupedTransactions result = new GroupedTransactions();
    for (Transaction transaction : transactions) {
      boolean matched = false;
      for (GroupRegexMatcher groupRegexMatcher : groupRegexMatchers) {
        if (groupRegexMatcher.matches(transaction)) {
          result.addToGroup(Group.of(groupRegexMatcher.group()), transaction);
          matched = true;
          break;
        }
      }
      if (!matched) {
        throw new IllegalStateException("at least one transaction could not be mapped to a group. please specifiy at least '.*' as fallback.");
      }
    }
    return result;
  }
}
