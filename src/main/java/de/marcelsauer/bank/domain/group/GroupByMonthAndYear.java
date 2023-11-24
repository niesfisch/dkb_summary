package de.marcelsauer.bank.domain.group;

import de.marcelsauer.bank.domain.Transaction;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * groups by month and year
 * e.g. Jan 2020, Feb 2020, Mar 2020, ...
 */
public final class GroupByMonthAndYear implements GroupBy {

  private final Collection<Transaction> transactions;

  public GroupByMonthAndYear(Collection<Transaction> transactions) {
    this.transactions = transactions;
  }

  private record MonthYear(Month month, Year year) {

  }

  @Override
  public GroupedTransactions get() {
    GroupedTransactions groupedTransactions = new GroupedTransactions();

    Map<MonthYear, List<Transaction>> rows = new HashMap<>();
    for (Transaction transaction : transactions) {
      Month month = transaction.wertstellung().getMonth();
      Year year = Year.of(transaction.wertstellung().getYear());
      MonthYear monthYear = new MonthYear(month, year);
      if (!rows.containsKey(monthYear)) {
        rows.put(monthYear, new ArrayList<>());
      }
      rows.get(monthYear).add(transaction);
    }
    for (Entry<MonthYear, List<Transaction>> monthYearSetEntry : rows.entrySet()) {
      MonthYear monthYear = monthYearSetEntry.getKey();
      String displayName = monthYear.month().getDisplayName(TextStyle.SHORT, Locale.GERMANY).replaceAll("\\.", "");
      displayName += " " + monthYear.year().getValue();
      groupedTransactions.addToGroup(Group.of(displayName), monthYearSetEntry.getValue());
    }
    return groupedTransactions;
  }
}
