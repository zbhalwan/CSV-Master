package edu.brown.cs32.examples.sprint3.csv.src.main.java.edu.brown.cs.student.main;



import edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions.FactoryFailureException;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing.CSVParser;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing.CreateList;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Searching.Search;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Searching.SearchException;

import java.io.*;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {

    // filename strToSearch colID hasHeaders

    try {
      Boolean hasHeaders = null;
      if (args.length == 4) {
        hasHeaders = Boolean.parseBoolean(args[3]);
      }

      Reader reader;
      try {
        reader = new FileReader(args[0]);
      } catch (FileNotFoundException e) {
        reader = new StringReader(args[0]);
      }

      CSVParser parser = new CSVParser(reader, hasHeaders, new CreateList());

      //      List<String> row3 = parser.getRow((ArrayList<String[]>) parser.getData(), 3);
      //      System.out.println(parser.Create(row3));

      Search sch = new Search(parser);

      if (args.length == 3) {
        sch.search(parser.getDataList(), args[1], args[2]);
      } else {
        sch.search(parser.getDataList(), args[1], null);
      }

    } catch (IOException | FactoryFailureException e) {
      System.err.println(e.getMessage());
    }
//    catch (ArrayIndexOutOfBoundsException e) {
//      System.err.println("The inputted column number is out of bounds.");
//    }
  }

  private void run() {
    // dear student: you can remove this. you can remove anything. you're in cs32. you're free!

  }
}
