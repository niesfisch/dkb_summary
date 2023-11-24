package de.marcelsauer.bank.config.plain;

import de.marcelsauer.bank.domain.filter.TransactionFilter;
import de.marcelsauer.bank.domain.filter.filter.AuftraggeberOderBeguenstigterTransactionFilter;
import de.marcelsauer.bank.domain.filter.filter.BetragGroesserAlsTransactionFilter;
import de.marcelsauer.bank.domain.filter.filter.BuchungstextTransactionFilter;
import de.marcelsauer.bank.domain.filter.filter.VerwendungszweckTransactionFilter;
import de.marcelsauer.bank.domain.group.matcher.AuftraggeberOderBeguenstigterGroupRegexMatcher;
import de.marcelsauer.bank.domain.group.matcher.BetragGroesserAlsGroupRegexMatcher;
import de.marcelsauer.bank.domain.group.matcher.BuchungstextGroupRegexMatcher;
import de.marcelsauer.bank.domain.group.matcher.GroupRegexMatcher;
import de.marcelsauer.bank.domain.group.matcher.VerwendungszweckGroupRegexMatcher;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {

  public final List<TransactionFilter> excludeFilters = new ArrayList<>();
  public final List<TransactionFilter> includeFilters = new ArrayList<>();
  public final List<GroupRegexMatcher> groupRegexMatchers = new ArrayList<>();

  public static Config from(Path file) {
    Config config = new Config();
    try {
      String csv = Files.readString(file, StandardCharsets.UTF_8);

      String[] lines = csv.split("\n");
      for (String line : lines) {
        if (line.isEmpty()) {
          continue;
        }
        if (line.startsWith("#")) {
          continue;
        }
        if (line.startsWith("filter.exclude")) {
          String[] split = line.replaceAll("filter\\.exclude\\.", "").split("=");
          TransactionFilter filter = getFilter(split[0], split[1]);
          config.excludeFilters.add(filter);
        } else if (line.startsWith("filter.include")) {
          String[] split = line.replaceAll("filter\\.include\\.", "").split("=");
          TransactionFilter filter = getFilter(split[0], split[1]);
          config.includeFilters.add(filter);
        } else if (line.startsWith("group.")) {
          // group.buchungstext=.*Gehalt.*||Gehalt
          String[] split = line.split("=");
          String colName = split[0].substring(split[0].indexOf('.') + 1);
          String[] valueSplit = split[1].split("\\|\\|");
          String regex = valueSplit[0];
          String group = valueSplit[1];
          GroupRegexMatcher matcher = getGroupRegexMatcher(colName, group, regex);
          config.groupRegexMatchers.add(matcher);
        }
      }
      return config;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static GroupRegexMatcher getGroupRegexMatcher(String colName, String group, String value) {
    GroupRegexMatcher result = switch (colName) {
      case "buchungstext" -> new BuchungstextGroupRegexMatcher(value, group);
      case "verwendungszweck" -> new VerwendungszweckGroupRegexMatcher(value, group);
      case "auftraggeber_oder_beguenstigter" -> new AuftraggeberOderBeguenstigterGroupRegexMatcher(value, group);
      case "betrag_groesser_als" -> new BetragGroesserAlsGroupRegexMatcher(new BigDecimal(value), group);
      default -> throw new IllegalArgumentException("could not find correct filter for '%s'".formatted(colName));
    };
    return result;
  }

  private static TransactionFilter getFilter(String colName, String value) {
    TransactionFilter result = switch (colName) {
      case "buchungstext" -> new BuchungstextTransactionFilter(value);
      case "verwendungszweck" -> new VerwendungszweckTransactionFilter(value);
      case "auftraggeber_oder_beguenstigter" -> new AuftraggeberOderBeguenstigterTransactionFilter(value);
      case "betrag_groesser_als" -> new BetragGroesserAlsTransactionFilter(new BigDecimal(value));
      default -> throw new IllegalArgumentException("could not find correct filter for '%s'".formatted(colName));
    };

    return result;
  }
}
