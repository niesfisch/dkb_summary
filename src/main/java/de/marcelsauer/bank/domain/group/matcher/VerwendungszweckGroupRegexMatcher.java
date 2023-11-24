package de.marcelsauer.bank.domain.group.matcher;

import de.marcelsauer.bank.domain.Transaction;
import java.util.regex.Pattern;

public class VerwendungszweckGroupRegexMatcher implements GroupRegexMatcher {

  private final String targetGroup;
  private final Pattern pattern;

  public VerwendungszweckGroupRegexMatcher(String regex, String targetGroup) {
    this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    this.targetGroup = targetGroup;
  }

  @Override
  public boolean matches(Transaction transaction) {
    return pattern.matcher(transaction.verwendungszweck()).find();
  }

  @Override
  public String group() {
    return targetGroup;
  }

  @Override
  public String toString() {
    return "VerwendungszweckGroupRegexMatcher [targetGroup=" + targetGroup + ", pattern=" + pattern + "]";
  }
}
