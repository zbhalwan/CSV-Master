package edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing;

import edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions.FactoryFailureException;

import java.util.List;

/**
 * This class implements the CreatorFromRow interface and converts the row into a List of strings.
 */
public class CreateStrings implements CreatorFromRow<StringBuilder> {
  @Override
  public StringBuilder create(List<String> row) throws FactoryFailureException {
    StringBuilder concatenatedString = new StringBuilder();

    for (String string : row) {
      concatenatedString.append(string).append(" ");
    }
    return concatenatedString;
  }
}
