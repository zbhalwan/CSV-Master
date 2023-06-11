package edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing;


import edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions.FactoryFailureException;

import java.util.List;

/**
 * Interface that allows developer to perform actions on row such as counting elements in it.
 *
 * @param <T> generic type T that the method will output
 */
public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException, FactoryFailureException;
}
