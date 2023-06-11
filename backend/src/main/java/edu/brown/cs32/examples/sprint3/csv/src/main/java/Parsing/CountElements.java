package edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing;


import edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions.FactoryFailureException;

import java.util.List;

public class CountElements implements CreatorFromRow<Integer> {
  @Override
  public Integer create(List<String> row) throws FactoryFailureException {
    return row.size();
  }
}
