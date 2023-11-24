package de.marcelsauer.bank.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
    LocalDate buchungstag,
    LocalDate wertstellung,
    String buchungstext,
    String auftraggeberBeguenstigter,
    String verwendungszweck,
    String kontonummer,
    String blz,
    BigDecimal betrag,
    String glaeubigerId,
    String mandatsreferenz,
    String kundenreferenz) {

  public boolean isGutschrift() {
    return this.betrag().compareTo(BigDecimal.ZERO) >= 1;
  }

  public boolean isBelastung() {
    return !isGutschrift();
  }

  public String toCsvLine() {

    return """
        "%s";"%s";"%s";"%s";"%s";"%s";"%s";"%s";"%s";"%s";"%s"
        """.formatted(
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

    );
  }
}
