package edu.brown.cs32.examples.sprint3.utilities;

import edu.brown.cs32.examples.sprint3.server.handlers.LoadHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.SearchHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.ViewHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.WeatherHandler;
import edu.brown.cs32.examples.sprint3.server.utilities.DataStorage;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FuzzTest {
    private static final Random randNum = new Random();

    /**
     * ran before everything to Set the Spark port number and to Remove the logging spam during tests.
     */
    @BeforeAll
    public static void setup_before_everything() {
        Spark.port(0);
        Logger.getLogger("").setLevel(Level.WARNING);
    }


    /**
     * ran before each test to Re-initialize state, etc. for _every_ test method run
     * and to restart the entire Spark server for every test! and to
     * not continue until the server is listening
     */
    @BeforeEach
    public void setup() {
        DataStorage.setData(null);
        LoadHandler loadHandler = new LoadHandler();
        WeatherCache cache = new WeatherCache();
        WeatherHandler weatherHandler = new WeatherHandler(cache);
        // In fact, restart the entire Spark server for every test!
        Spark.get("/loadcsv", loadHandler);
        Spark.get("/searchcsv", new SearchHandler(loadHandler));
        Spark.get("/viewcsv", new ViewHandler(loadHandler));
        Spark.get("/weather", weatherHandler);
        Spark.init();
        Spark.awaitInitialization(); // don't continue until the server is listening
    }

    /**
     *  ran after each test to Gracefully stop Spark listening on both endpoints and
     *  not proceed until the server is stopped
     */
    @AfterEach
    public void teardown() {
        // Gracefully stop Spark listening on both endpoints
        Spark.unmap("/loadcsv");
        Spark.unmap("/searchcsv");
        Spark.unmap("/viewcsv");
        Spark.unmap("/weather");

        Spark.awaitStop(); // don't proceed until the server is stopped
    }


    /**
     * Helper to start a connection to a specific API endpoint/params
     * @param apiCall the call string, including endpoint
     *                (NOTE: this would be better if it had more structure!)
     * @return the connection for the given URL, just after connecting
     * @throws IOException if the connection fails for some reason
     */
    static private HttpURLConnection tryRequest(String apiCall) throws IOException {
        // Configure the connection (but don't actually send the request yet)
        URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

        // The default method is "GET", which is what we're using here.
        // If we were using "POST", we'd need to say so.
        //clientConnection.setRequestMethod("GET");

        clientConnection.connect();
        return clientConnection;
    }


    /**
     * Generates Random Number
     */
    public static int randomNumGen(int min, int max) {
        return randNum.nextInt(max) + min;
    }


    /**
     * Generates random string of certain length.
     * Comment out <int randomAscii = randomNum.nextInt(45, 127);> and write in
     * <int randomAscii = randomNum.nextInt(96) + 32;> in order to test with random spacing in between.
     *
     */
    public String randomStringGen(int length) {
        StringBuilder string = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            //int randomAscii = randomNum.nextInt(96) + 32;
            //no spaces
            int randomAscii = randNum.nextInt(45, 127);
            string.append((char) randomAscii);
        }
        return string.toString();
    }



    /**
     * Tests loading any random filepath.
     * should not fail because error for loading invalid filepath has been accounted for
     */
    @Test
    public void testFuzzLoadCSV() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            String input = randomStringGen(rand);
            HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=" + input);
            int status = clientConnection.getResponseCode();

            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }

    }

    /**
     * Fuzz tests load with header
     * should not fail because error for loading invalid filepath has been accounted for
     */
    @Test
    public void testFuzzLoadCSVWithHeader() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand1 = randomNumGen(0,100);
            int rand2 = randomNumGen(0,100);
            String string1 = randomStringGen(rand1);
            String string2 = randomStringGen(rand2);
            HttpURLConnection clientConnection = tryRequest("loadcsv?filepath="+string1+"&header="+string2);
            int status = clientConnection.getResponseCode();

            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }


    }


    /**
     * Tests view with random input.
     * should not fail because error for loading multiple args w view has been accounted for
     */
    @Test
    public void testFuzzViewCSV() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            String input = randomStringGen(rand);
            HttpURLConnection clientConnection = tryRequest("viewcsv?" + input);
            int status = clientConnection.getResponseCode();

            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }

    }


    /**
     * Tests Search with random desired value
     * test will fail if status not 200
     */
    @Test
    public void fuzzSearchNoColTest() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            String input = randomStringGen(rand);
            HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv");
            assertEquals(200, clientConnection.getResponseCode());
            clientConnection = tryRequest("searchcsv?value=" + input);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }


    /**
     * Tests Search with random desired value
     * test will fail if status not 200
     */
    @Test
    public void fuzzSearchColTest() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            int rand2 = randomNumGen(0,100);
            String input = randomStringGen(rand);
            String input2 = randomStringGen(rand2);
            HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");
            assertEquals(200, clientConnection.getResponseCode());
            clientConnection = tryRequest("searchcsv?value=" + input+"&columID=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }

    /**
     * searching without file
     * test will fail if status not 200
     */
    @Test
    public void fuzzSearchWithoutFile() throws IOException {
        for (int i=0; i<1000; i++ ) {
            int rand = randomNumGen(0,100);
            int rand2 = randomNumGen(0,100);
            String input = randomStringGen(rand);
            String input2 = randomStringGen(rand2);
            HttpURLConnection clientConnection = tryRequest("searchcsv?value=" + input+"&columID=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }



    /**
     * fuzz test weather with improper lat and lon values
     * test will fail if status not 200
     */
    @Test
    public void fuzzWeatherWithWrongInputs() throws IOException {
        for (int i = 0; i < 1000; i++) {
            int rand = randomNumGen(0, 100);
            int rand2 = randomNumGen(0, 100);
            String input = randomStringGen(rand);
            String input2 = randomStringGen(rand2);
            HttpURLConnection clientConnection = tryRequest("weather?lat=" + input + "&lon=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }



    /**
     * fuzz test weather with correct lat and lon values
     * test will fail if status not 200
     */
    @Test
    public void fuzzWeatherWithCorrectInputs() throws IOException {
        for (int i = 0; i < 1000; i++) {
            int rand = randomNumGen(-90, 90);
            int rand2 = randomNumGen(-180, 180);
            String input = Integer.toString(rand);
            String input2 = Integer.toString(rand2);
            HttpURLConnection clientConnection = tryRequest("weather?lat=" + input + "&lon=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }


    /**
     * fuzz test weather with abnormal lat and lon values
     * test will fail if status not 200
     */
    @Test
    public void fuzzWeatherWithAbnormalInputs() throws IOException {
        for (int i = 0; i < 1000; i++) {
            int rand = randomNumGen(1000000, 2000000);
            int rand2 = randomNumGen(1000000, 2000000);
            String input = Integer.toString(rand);
            String input2 = Integer.toString(rand2);
            HttpURLConnection clientConnection = tryRequest("weather?lat=" + input + "&lon=" + input2);
            int status = clientConnection.getResponseCode();
            if (status != 200) {
                fail("invalid status" + status);
            }
            clientConnection.connect();
            clientConnection.disconnect();
        }
    }














    }






