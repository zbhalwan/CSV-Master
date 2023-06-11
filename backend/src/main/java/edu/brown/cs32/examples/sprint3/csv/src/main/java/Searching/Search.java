package edu.brown.cs32.examples.sprint3.csv.src.main.java.Searching;


import edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing.CSVParser;

import java.util.List;

/**
 * Search class that searches through CSV for desired value
 *
 * @param <T>
 */
public class Search<T> {

  private CSVParser<T> parser;

  public Search(CSVParser parser) {
    this.parser = parser;
  }

  boolean isValueFound = false;

  /**
   * method that searches through parsed CSV to find desired value
   *
   * @param dataVals parsed csv values
   * @param value value that user is searching for
   * @param column value to identify column
   */
  public List search(List<List<String>> dataVals, String value, String column) throws ArrayIndexOutOfBoundsException, NumberFormatException {

    if (column == null) {
      for (List<String> row : dataVals) {
        for (String val : row) {
          if (val.equals(value)) {
            isValueFound = true;
            System.out.println(row);
            return row;
          }
        }
      }

    } else {
      try {
        int index = Integer.parseInt(column);
        for (List<String> row : dataVals) {
          if (row.get(index).equals(value)) {
            isValueFound = true;
            System.out.println(row);
            return row;
          }
        }

      }
      catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("The inputted column number is out of bounds.");
      }
      catch (NumberFormatException e) {
        if (this.parser.getHasHeaders()) {
          int index = -1;
          List<String> headers = dataVals.get(0);
          for (int i = 0; i < headers.size(); i++) {
            if ((headers).get(i).equals(column)) {
              index = i;
            }
          }
          if (index == -1) {
            System.err.println("The column " + column + " does not contain the desired value.");
          } else {
            for (int i = 1; i < dataVals.size(); i++) {
              List<String> row = dataVals.get(i);
              if (row.get(index).equals(value)) {
                isValueFound = true;
                System.out.println(row);
                return row;
              }
            }
          }
        } else {
          System.err.println("CSV has no headers, use index");
        }
      }
    }
    if (!isValueFound) {
      System.err.println("item not found.");
    }
    return null;
  }

  /**
   * getter method for valueFound
   * @return whther valueFound true or false
   */
  public boolean isValueFound() {
    return this.isValueFound;
  }
}
