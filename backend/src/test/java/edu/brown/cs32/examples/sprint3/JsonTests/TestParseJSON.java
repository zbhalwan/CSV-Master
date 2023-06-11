package edu.brown.cs32.examples.sprint3.JsonTests;

import edu.brown.cs32.examples.sprint3.JsonHandlers.ParseJSON;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestParseJSON {
    /**
     * helper method that converts mock json file to string
     * @return json file as string
     */
    private String readJSON() {
        String jsonString = "";
        Path mockFilepath = Path.of("src/main/java/edu/brown/cs32/examples/sprint3/JsonHandlers/mockJSON.json");
        try {
            jsonString = Files.readString(mockFilepath);
        } catch (IOException e) {
            System.err.println("Unable to parse JSON. Error: " + e.getMessage());
        }
        return jsonString;
    }
    /**
     * helper method that tries to convert nonexistent json file to string
     * @return json file as string
     */
    private String readWrongJSON() {
        String jsonString = "";
        Path mockFilepath = Path.of("src/main/java/edu/brown/cs32/examples/sprint3/JsonHandlers/wrongJSON.json");
        try {
            jsonString = Files.readString(mockFilepath);
        } catch (IOException e) {
            System.err.println("Unable to parse JSON. Error: " + e.getMessage());
        }
        return jsonString;
    }


    /**
     * tests basic data (different types) retrieval from mock JSON
     * @throws IOException
     */
    @Test
    public void testBasicReadJSON() throws IOException {
        String jsonString = this.readJSON();
        ParseJSON JSONParser = new ParseJSON(jsonString);

        Map<String, Object> parsedJSON = JSONParser.getData();
        assertEquals("Steak", parsedJSON.get("Favorite Food"));
        assertEquals("risk it all", parsedJSON.get("Favorite Quote"));
        assertEquals(20.0, parsedJSON.get("Age"));
        assertEquals(Arrays.asList(-77.0, 38.0), parsedJSON.get("Numbers"));
        assertEquals(Arrays.asList(false, true), parsedJSON.get("Bool"));
        assertEquals(Arrays.asList(Arrays.asList("happy", "sad")), parsedJSON.get("2d"));
    }

    /**
     * tests data retrieval from data that does not exist in JSON
     * @throws IOException
     */
    @Test
    public void testNullReadJSON() throws IOException {
        String jsonString = this.readJSON();
        ParseJSON JSONParser = new ParseJSON(jsonString);

        Map<String, Object> parsedJSON = JSONParser.getData();
        assertNull(parsedJSON.get("Favorite Toy"));
        assertNull(parsedJSON.get("Color"));
    }

    /**
     * testing invalid file path
     * @throws IOException
     */
    @Test
    public void testReadWrongJSON() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream oldErr = System.err;
        System.setErr(printStream);

        String jsonString2 = this.readWrongJSON();
//        ParseJSON JSONParser = new ParseJSON(jsonString2);

        // restore the original System.err
        System.setErr(oldErr);

        // assert the output
        assertEquals("Unable to parse JSON. Error: src/main/java/edu/brown/cs32/examples/sprint3/JsonHandlers/wrongJSON.json", outputStream.toString().trim());
    }


}
