package edu.brown.cs32.examples.sprint3.server.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * class that stores data globally
 */
public class DataStorage {

    private static List<List<String>> currentData;

    private static boolean header;


    /**
     * creates a deep copy of the current data
     * @return a deepcopy of current data
     */
    public static List copyCurrentData() {
        if (currentData == null) return null;
        ArrayList<List<String>> deepCopy = new ArrayList<>();
        for (List<String> str : currentData) {
            deepCopy.add(new ArrayList<>(str));
        }
        return deepCopy;
    }

    /**
     * sets the data being parsed as the current data
     * @param currData
     */
    public static void setData(List currData) {
        DataStorage.currentData = currData;
    }
    /**
     * gets whether headers are present
     * @return boolean if headers are present or not
     */
    public static boolean isHeader() {
        return header;
    }

    /**
     * sets the header value to the queried header paramenter by user
     * @param header
     */
    public static void setHeader(boolean header) {
        DataStorage.header = header;
    }

    /**
     * gets the current data
     * @return the current data
     */
    public static List<List<String>> getCurrentData() {
        return currentData;
    }


}

