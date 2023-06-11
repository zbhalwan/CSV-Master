package edu.brown.cs32.examples.sprint3.handlers;

import edu.brown.cs32.examples.sprint3.server.handlers.LoadHandler;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestLoadCSVHandler {

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
        // Re-initialize state, etc. for _every_ test method run

        // In fact, restart the entire Spark server for every test!
        Spark.get("/loadcsv", new LoadHandler());
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
     * tests when no file is loaded ie no filepath param
     * @throws IOException
     */
    @Test
    public void testNoLoadedFile() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("filepath needed", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

        clientConnection.disconnect();

    }

    /**
     * testing loadcsv with no header
     * @throws IOException
     */
    @Test
    public void testNoHeaderByDefault() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("success", resp.get("result"));
        assertEquals("rand.csv", resp.get("filepath"));
        assertEquals("false", resp.get("header"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

        clientConnection.disconnect();
    }

    /**
     * testing loadcsv with no header using query param
     * @throws IOException
     */
    @Test
    public void testNoHeaderFalseInput() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv&header=false");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("success", resp.get("result"));
        assertEquals("rand.csv", resp.get("filepath"));
        assertEquals("false", resp.get("header"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

        clientConnection.disconnect();
    }

    /**
     * testing loadcsv with header
     * @throws IOException
     */
    @Test
    public void testNoHeaderTrueInput() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=stardata.csv&header=True");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("success", resp.get("result"));
        assertEquals("stardata.csv", resp.get("filepath"));
        assertEquals("true", resp.get("header"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

        clientConnection.disconnect();
    }

    /**
     * tests loadcsv when nonexistent file is loaded
     * @throws IOException
     */
    @Test
    public void testNonexistentFile() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=badfile.csv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_datasource", resp.get("result"));
        assertEquals("The file badfile.csv not found.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

        clientConnection.disconnect();
    }

}
