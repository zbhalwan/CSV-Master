package edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions;

import java.util.ArrayList;
import java.util.List;

public class FactoryFailureException extends Exception {
  public final List<String> row;

  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
