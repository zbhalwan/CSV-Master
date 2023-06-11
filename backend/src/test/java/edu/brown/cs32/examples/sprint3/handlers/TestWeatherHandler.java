package edu.brown.cs32.examples.sprint3.handlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherCache;
import edu.brown.cs32.examples.sprint3.server.handlers.WeatherHandler;
import edu.brown.cs32.examples.sprint3.server.utilities.Serialize;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherClass;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestWeatherHandler {
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
        WeatherCache cache = new WeatherCache();
        WeatherHandler weatherHandler = new WeatherHandler(cache);

        // In fact, restart the entire Spark server for every test!
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
     * testing if no lat and lon params are given
     * @throws IOException
     */
    @Test
    public void testNoLatLon() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("Please call using 'lat, lon': lat and lon are missing", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

        clientConnection.disconnect();

    }

    /**
     * testing if no lon param is given
     * @throws IOException
     */
    @Test
    public void testNoLon() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=41.8240");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("Please call using 'lat, lon': lon was missing", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * testing if no lat param is given
     * @throws IOException
     */
    @Test
    public void testNoLat() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lon=-71.4128");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("Please call using 'lat, lon': lat was missing", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * testing if numbers aren't passed in to lat/lon
     * @throws IOException
     */
    @Test
    public void testLatLonWithoutNumbers() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=happy&lon=-71.4128");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("Number Format was incorrect.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * testing invalid lat and lon
     * @throws IOException
     */
    @Test
    public void testNonexistentLatLon() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=-91&lon=-188");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("Data is now out of bounds, please enter a latitude between -90 and 90, and a longitude between -180 and 180.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * testing lat and lon which the api doesn't have data for
     * @throws IOException
     */
    @Test
    public void testCoordinatesWithNoData() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=30&lon=30");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_bad_request", resp.get("result"));
        assertEquals("Grid response returned null-- there is no data for the point 30,30.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    @Test
    public void testCoordinatesWithData() throws IOException {
        HttpURLConnection clientConnection = tryRequest("weather?lat=41.8240&lon=-71.4128");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("success", resp.get("result"));
        assertEquals(-71.4128, resp.get("Longitude"));
        assertEquals(41.824, resp.get("Latitude"));
        assertNotNull(resp.get("Temperature"));
        assertNotNull(resp.get("Unit"));
        assertEquals("F", resp.get("Unit"));
        assertNotNull(resp.get("Date and Time Retrieved"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

}
