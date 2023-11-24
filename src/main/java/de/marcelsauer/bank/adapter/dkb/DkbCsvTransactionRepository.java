package de.marcelsauer.bank.adapter.dkb;

import de.marcelsauer.bank.domain.Transaction;
import de.marcelsauer.bank.domain.TransactionRepository;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class DkbCsvTransactionRepository implements TransactionRepository {

  private final Path csvFilePath;

  public DkbCsvTransactionRepository(Path csvFilePath) {
    this.csvFilePath = csvFilePath;
  }

  @Override
  public Collection<Transaction> load() {
    try {
      String csv = Files.readString(csvFilePath, StandardCharsets.ISO_8859_1);
      return new DkbCsvToTransactions(csv).parse();
    } catch (Exception e) {
      throw new IllegalArgumentException("could not load csf file ..", e);
    }
  }
}
