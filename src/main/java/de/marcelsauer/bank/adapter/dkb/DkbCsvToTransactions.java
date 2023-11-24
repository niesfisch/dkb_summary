package de.marcelsauer.bank.adapter.dkb;

import de.marcelsauer.bank.domain.Transaction;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

public class DkbCsvToTransactions {

  private final String csv;

  public DkbCsvToTransactions(String dkbCsv) {
    this.csv = dkbCsv;
  }

  public Collection<Transaction> parse() {
    Collection<String> alreadyProcessed = new HashSet<>();
    Collection<Transaction> transactions = new ArrayList<>();
    String[] lines = csv.split("\n");
    for (String line : lines) {
      if (line.isEmpty()) {
        continue;
      }
      // ignore duplicate lines that can happen if you join multiple csv files
      String sha1 = sha1(line);
      if (alreadyProcessed.contains(sha1)) {
        continue;
      }
      // skip lines that do not begin with ... "30.12.2021";... e.g. when csv files are merged
      if (!line.matches("\"\\d+.*")) {
        continue;
      }
      // "Buchungstag";"Wertstellung";"Buchungstext";"Auftraggeber / Begünstigter";"Verwendungszweck";"Kontonummer";"BLZ";"Betrag (EUR)";"Gläubiger-ID";"Mandatsreferenz";"Kundenreferenz";
      String[] columns = line.split(";");

      LocalDate buchungstag = toLocalDate(columns[0]);
      LocalDate wertstellung = toLocalDate(columns[1]);

      String buchungstext = clean(columns[2]);
      String auftraggeberBeguenstigter = clean(columns[3]);
      String verwendungszweck = clean(columns[4]);
      String kontonummer = clean(columns[5]);
      String blz = clean(columns[6]);
      // handle stuff like -1.750,00
      BigDecimal betrag =
          new BigDecimal(
              clean(columns[7])
                  .replaceAll("\\.", "")
                  .replaceAll(",", ".")
          );
      String glaeubigerId = clean(columns[8]);
      String mandatsreferenz = clean(columns[9]);
      String kundenreferenz = clean(columns[10]);
      try {
        transactions.add(new Transaction(
            buchungstag,
            wertstellung,
            buchungstext,
            auftraggeberBeguenstigter,
            verwendungszweck,
            kontonummer,
            blz,
            betrag,
            glaeubigerId,
            mandatsreferenz,
            kundenreferenz
        ));
      } catch (Exception e) {
        throw new RuntimeException("could not parse row %s".formatted(line), e);
      }
      alreadyProcessed.add(sha1);
    }
    return transactions;
  }

  private LocalDate toLocalDate(String value) {
    DateTimeFormatter germanFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.GERMAN);
    return LocalDate.parse(clean(value), germanFormatter);
  }

  private String clean(String value) {
    return value.replaceAll("\"", "").trim();
  }

  private String sha1(String value) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    md.update(value.getBytes(StandardCharsets.UTF_8));
    byte[] digest = md.digest();
    StringBuilder hexString = new StringBuilder();
    for (byte b : digest) {
      hexString.append(String.format("%02x", b));
    }
    return hexString.toString();
  }
}
