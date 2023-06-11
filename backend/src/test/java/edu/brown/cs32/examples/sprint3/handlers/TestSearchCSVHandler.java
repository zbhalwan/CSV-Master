package edu.brown.cs32.examples.sprint3.handlers;

import edu.brown.cs32.examples.sprint3.server.handlers.LoadHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.SearchHandler;
import edu.brown.cs32.examples.sprint3.server.utilities.DataStorage;
import edu.brown.cs32.examples.sprint3.server.utilities.Serialize;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSearchCSVHandler {

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
        // In fact, restart the entire Spark server for every test!
        Spark.get("/loadcsv", loadHandler);
        Spark.get("/searchcsv", new SearchHandler(loadHandler));

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
     * tests searchcsv without a loaded file
     * @throws IOException
     */
    @Test
    public void testSearchWithNoLoadedFile() throws IOException {
        HttpURLConnection clientConnection = tryRequest("searchcsv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_datasource", resp.get("result"));
        assertEquals("No file loaded. Load file using loadcsv endpoint and query parameter 'filename'.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }


    /**
     * tests searchcsv without a desired value query param
     * @throws IOException
     */
    @Test
    public void testSearchWithoutQuery() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("Please specify desired value", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * basic test for searchcsv on csv with no headers
     * @throws IOException
     */
    @Test
    public void testSearchWithoutHeaders() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for searchcsv on csv with no headers using nonexistent value in csv
     * @throws IOException
     */
    @Test
    public void testSearchWithoutHeadersWithIncorrectQuery() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=zeeshan");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("desired value -zeeshan- was not found.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for searchcsv on csv with no headers utilizing correct column index
     * @throws IOException
     */
    @Test
    public void testSearchWithoutHeadersWithColumnIndex() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=1");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for searchcsv on csv with no headers with incorrect column index
     * @throws IOException
     */
    @Test
    public void testSearchWithoutHeadersWithWrongColumnIndex() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=2");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("The inputted column number -2- does not contain desired value. " +
                "Use a different column index or simply just pass in the desired value " +
                "without a column identifier.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for searchcsv on csv with no headers using column index that doesn't exist
     * @throws IOException
     */
    @Test
    public void testSearchWithoutHeadersWithOutOfBoundsColumnIndex() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=99");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("The inputted column number -99- is out of bounds. There are 5 columns. column index is zero-indexed.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for searchcsv on csv with no headers using a random nonexistent header value
     * @throws IOException
     */
    @Test
    public void testSearchWithoutHeadersWithHeaders() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=ProperName");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("CSV has no headers, use column index or simply just " +
                "pass in the desired value without a column identifier.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for search csv with headers using correct column index
     * @throws IOException
     */
    @Test
    public void testSearchWithHeadersColIndex() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=1");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for search csv with headers using correct column header
     * @throws IOException
     */
    @Test
    public void testSearchWithHeadersColHeader() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=ProperName");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for search csv with headers using incorrect column index
     * @throws IOException
     */
    @Test
    public void testSearchWithHeadersWithWrongColumnIndex() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=2");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("The inputted column number -2- does not contain desired value. " +
                "Use a different column index or simply just pass in the desired value " +
                "without a column identifier.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for search csv with headers using nonexistent column index
     * @throws IOException
     */
    @Test
    public void testSearchWithHeadersWithOutOfBoundsColumnIndex() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=99");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("The inputted column number -99- is out of bounds. There are 5 columns. column index is zero-indexed.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for search csv with headers using incorrect header
     * @throws IOException
     */
    @Test
    public void testSearchWithHeadersWithWrongHeader() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=Random");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("The column Random does not contain the desired value. Here is a list of present column headers: [StarID, ProperName, X, Y, Z]", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();

    }

    /**
     * test for search csv with headers using correct incorrect header but header exists in csv
     * @throws IOException
     */
    @Test
    public void testSearchWithHeadersWithWrongHeader2() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=StarID");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("The column StarID does not contain the desired value. Here is a list of present column headers: [StarID, ProperName, X, Y, Z]", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();

    }

    /**
     * test for search csv with headers using incorrect desired value param
     * @throws IOException
     */
    @Test
    public void testSearchWithHeadersWithIncorrectQuery() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=zeeshan");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("desired value -zeeshan- was not found.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test for search csv when loading different csv files
     * @throws IOException
     */
    @Test
    public void testSearchWithDifferentLoadFiles() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");
        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("loadcsv?filepath=smallstardata.csv&header=true");
        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=ProperName");
        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    // test search twice




}
