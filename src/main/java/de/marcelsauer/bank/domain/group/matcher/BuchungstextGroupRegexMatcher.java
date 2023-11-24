package de.marcelsauer.bank.domain.group.matcher;

import de.marcelsauer.bank.domain.Transaction;
import java.util.regex.Pattern;

public class BuchungstextGroupRegexMatcher implements GroupRegexMatcher {

  private final String targetGroup;
  private final Pattern pattern;

  public BuchungstextGroupRegexMatcher(String regex, String targetGroup) {
    this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    this.targetGroup = targetGroup;
  }

  @Override
  public boolean matches(Transaction transaction) {
    return pattern.matcher(transaction.buchungstext()).find();
  }

  @Override
  public String group() {
    return targetGroup;
  }

  @Override
  public String toString() {
    return "BuchungstextGroupRegexMatcher [targetGroup=" + targetGroup + ", pattern=" + pattern + "]";
  }
}
