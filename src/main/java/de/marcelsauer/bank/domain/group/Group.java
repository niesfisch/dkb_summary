package de.marcelsauer.bank.domain.group;

public record Group(String group) {

  public static Group of(String group) {
    return new Group(group);
  }

  public String name() {
    return group;
  }
}
