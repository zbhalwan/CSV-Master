package edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing;


import edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions.FactoryFailureException;

import java.util.List;

/**
 * This class implements the CreatorFromRow interface and converts the row into a List of strings.
 */
public class CreateList implements CreatorFromRow<List<String>> {
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
