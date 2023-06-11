package edu.brown.cs32.examples.sprint3.utilities;

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs32.examples.sprint3.server.utilities.Serialize;
import edu.brown.cs32.examples.sprint3.server.utilities.Unserialize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestSerializeAndUnserialize {

    /**
     * test basic success message
     */
    @Test
    public void testBasicSerializeSuccess() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        assertEquals("{\"result\":\"success\"}", Serialize.success(map));
    }

    /**
     * test success meesage with multiple outputs
     */
    @Test
    public void testSerializeSuccessMultipleOutputs() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        map.put("filename", "stardata.csv");
        assertEquals("{\"result\":\"success\",\"filename\":\"stardata.csv\"}", Serialize.success(map));
    }

    /**
     * regular error message
     */
    @Test
    public void testSerializeError() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("error_datasource", "no file");
        assertEquals("{\"result\":\"error_datasource\",\"message\":\"no file\"}", Serialize.error("error_datasource", "no file"));
    }

    /**
     * testing serialize success with list of list of strings
     */
    @Test
    public void testSerializeSuccessWithListofListofStrings() {
        HashMap<String, Object> map = new HashMap<>();
        List<List<String>> listOfStrings = Arrays.asList(Arrays.asList("zeeshan", "bhalwani"));

        map.put("result", "success");
        map.put("data", listOfStrings);

        String jsonWithListOfListOfStrings = "{\"result\":\"success\",\"data\":[[\"zeeshan\",\"bhalwani\"]]}";

        assertEquals(jsonWithListOfListOfStrings, Serialize.success(map));
    }

    /**
     * testing unserialize with valid json
     * @throws IOException
     */
    @Test
    void testUnserializeWithValidJson() throws IOException {
        String validJson = "{\"result\":\"success\", \"filepath\":\"stardata.csv\"}";
        Map<String,Object> expected = new HashMap<>();
        expected.put("result", "success");
        expected.put("filepath", "stardata.csv");

        Map<String,Object> actual = Unserialize.unserialize(validJson);

        Assertions.assertEquals(expected, actual);
    }

    /**
     * testing unserialize with invalid json
     */
    @Test
    void testUnserializeWithInvalidJson() {
        String invalidJson = "{result:\"success\", filepath:stardata.csv}";

        Assertions.assertThrows(IOException.class, () -> Unserialize.unserialize(invalidJson));
    }

    /**
     * testing unserialize with list of list of strings
     * @throws IOException
     */
    @Test
    void testUnserializeWithListOfListOfStrings() throws IOException {
        String jsonWithListOfListOfStrings = "{\"result\":\"success\", \"data\":[[\"zeeshan\", \"bhalwani\"]]}";
        Map<String, Object> expected = new HashMap<>();
        expected.put("result", "success");
        List<List<String>> listOfStrings = Arrays.asList(Arrays.asList("zeeshan", "bhalwani"));
        expected.put("data", listOfStrings);

        Map<String, Object> actual = Unserialize.unserialize(jsonWithListOfListOfStrings);

        Assertions.assertEquals(expected, actual);
    }





}
