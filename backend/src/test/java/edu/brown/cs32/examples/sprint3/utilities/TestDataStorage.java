package edu.brown.cs32.examples.sprint3.utilities;

import edu.brown.cs32.examples.sprint3.server.utilities.DataStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataStorage {

    /**
     * before each test set header to false and empty the data
     */
    @BeforeEach
    void setUp() {
        DataStorage.setHeader(false);
        DataStorage.setData(null);
    }

    /**
     * testing setting and getting the header
     */
    @Test
    public void testSetAndGetHeader() {
        boolean expected = true;
        DataStorage.setHeader(expected);
        boolean actual = DataStorage.isHeader();
        Assertions.assertEquals(expected, actual);
    }

    /**
     * testing setting and getting mock data
     */
    @Test
    public void testSetAndGetCurrentData() {
        List<List<String>> csvData = new ArrayList<>();
        String csvString = "0,Sol,0,0,0\n" +
                "1,Andreas,282.43485,0.00449,5.36884\n" +
                "2,Andreas,43.04329,0.00285,-15.24144\n" +
                "9,Andreas,43.04329,0.00285,-15.24144\n" +
                "3,Mortimer,277.11358,0.02422,223.27753\n" +
                "4,Bailee,79.62896,0.01164,-101.53103\n" +
                "5,Mortimer,27.11358,0,225.27753\n";
        String[] lines = csvString.split("\n");
        for (String line : lines) {
            List<String> row = Arrays.asList(line.split(","));
            csvData.add(row);
        }

        DataStorage.setData(csvData);
        List<List<String>> actual = DataStorage.getCurrentData();
        Assertions.assertEquals(csvData, actual);

    }

    /**
     * null test on deep copying the data
     */
    @Test
    public void testCopyCurrentDataWithNull() {
        List<List<String>> actual = DataStorage.copyCurrentData();
        Assertions.assertNull(actual);
    }

    /**
     * testing deep copying mock data
     */
    @Test
    public void testCopyCurrentData() {
        List<List<String>> csvData = new ArrayList<>();
        String csvString = "0,Sol,0,0,0\n" +
                "1,Andreas,282.43485,0.00449,5.36884\n" +
                "2,Andreas,43.04329,0.00285,-15.24144\n" +
                "9,Andreas,43.04329,0.00285,-15.24144\n" +
                "3,Mortimer,277.11358,0.02422,223.27753\n" +
                "4,Bailee,79.62896,0.01164,-101.53103\n" +
                "5,Mortimer,27.11358,0,225.27753\n";
        String[] lines = csvString.split("\n");
        for (String line : lines) {
            List<String> row = Arrays.asList(line.split(","));
            csvData.add(row);
        }
        DataStorage.setData(csvData);
        List<List<String>> actual = DataStorage.copyCurrentData();
        Assertions.assertEquals(csvData, actual);
        Assertions.assertNotSame(csvData, actual);
    }

}
