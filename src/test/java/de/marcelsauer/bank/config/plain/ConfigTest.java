package de.marcelsauer.bank.config.plain;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class ConfigTest {

  @Test
  void shouldLoadConfigHappyPath() {
    // given
    Path file = Paths.get("src/test/resources/test.config");

    // when
    Config from = Config.from(file);

    // then
    assertThat(from.includeFilters.size()).isEqualTo(4);
    assertThat(from.excludeFilters.size()).isEqualTo(5);
    assertThat(from.groupRegexMatchers.size()).isEqualTo(26);
  }
}