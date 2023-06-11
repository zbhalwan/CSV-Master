package edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing;


import edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions.FactoryFailureException;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing.CreatorFromRow;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing.ICSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that parses a csv file or string representing a csv.
 *
 * @param <T> generic type T that a row can take form
 */
public class CSVParser<T> implements ICSVParser {
  private Reader reader;
  private List<String[]> data;
  private List<T> dataList;
  private int numColumns;
  private Boolean hasHeaders;
  private CreatorFromRow<T> creator;

  /**
   * Constructor for the CSV parser.
   *
   * @param reader the file or the string representing the data
   * @param gotHeaders input from the user whether the CSV contains headers
   * @param creator a class which implements the CreatorFromRow interface allowing developer to
   *     easily convert the row into a different type
   * @throws IOException
   * @throws FactoryFailureException
   */
  public CSVParser(Reader reader, Boolean gotHeaders, CreatorFromRow<T> creator)
      throws IOException, FactoryFailureException {
    this.reader = reader;
    this.hasHeaders = gotHeaders;
    this.creator = creator;
    parse();
  }

  /**
   * method that parses a csv file or string and adds the data to data and dataList structures
   *
   * @throws IOException
   * @throws FactoryFailureException
   */
  public void parse() throws IOException, FactoryFailureException {
    this.data = new ArrayList<String[]>();
    this.dataList = new ArrayList<>();
    BufferedReader br = new BufferedReader(this.reader);
    String line = br.readLine();
    int lineNum = 1;
    while (line != null) {
      String[] row = line.split(",");
      T convT = this.creator.create(List.of(row));
      // add catch block
      this.dataList.add(convT);
      this.data.add(row);

      lineNum++;
      line = br.readLine();
    }

    br.close();
  }

  /**
   * getter method for DataList
   * @return list
   */
  public List<T> getDataList() {
    return this.dataList;
  }

  /**
   * method gets row from data list based on row index
   * @param data data list
   * @param rowInd row index
   * @return desired row as list of strings
   */
  public List<String> getRow(ArrayList<String[]> data, int rowInd) {
    return List.of(data.get(rowInd));
  }

  /**
   * utilizes creatorFromRow interface to perform action on data
   * @param row
   * @return
   * @throws FactoryFailureException
   */
  public T Create(List<String> row) throws FactoryFailureException {
    T convRow = null;
    try {
      convRow = this.creator.create(row);
    } catch (FactoryFailureException e) {
      System.err.println("The input was not able to be converted.");
    }
    return convRow;
  }

  /**
   * method that gets num of columns
   * @return num of columns
   */
  @Override
  public int getNumColumns() {
    return this.numColumns;
  }

  /**
   * gets whether headers are present
   * @return boolean if headers are present or not
   */
  @Override
  public Boolean getHasHeaders() {
    return this.hasHeaders;
  }

  /**
   * method that creates a deepcopy of the data list to prevent mutation
   * @return copy of data
   */
  @Override
  public List<List<String>> getData() {
    List<List<String>> newList = new ArrayList<>();
    for (String[] stringArray : this.data) {
      List<String> stringList = new ArrayList<>();
      for (String string : stringArray) {
        stringList.add(string);
      }
      newList.add(stringList);
    }
    return newList;
  }


}
