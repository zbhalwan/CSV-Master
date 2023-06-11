package edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing;

import java.util.List;

public interface ICSVParser<T> {
  int getNumColumns();

  Boolean getHasHeaders();

  List<T> getData();
}
