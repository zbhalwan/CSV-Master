package edu.brown.cs32.examples.sprint3.handlers;
import edu.brown.cs32.examples.sprint3.server.handlers.LoadHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.SearchHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.ViewHandler;
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

public class TestViewCSVHandler {
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
        Spark.get("/viewcsv", new ViewHandler(loadHandler));
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
     * test view when no file has been loaded
     * @throws IOException
     */
    @Test
    public void testViewWithNoLoadedFile() throws IOException {
        HttpURLConnection clientConnection = tryRequest("viewcsv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        assertEquals("error_datasource", resp.get("result"));
        assertEquals("No file loaded. Load file using loadcsv endpoint and query parameter 'filename'.", resp.get("message"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * basic test for view with file with no header
     * @throws IOException
     */
    @Test
    public void testViewWithOneLoadedFileNoHeader() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("viewcsv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

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


        assertEquals("success", resp.get("result"));
        assertEquals(csvData, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * basic test for view on file with headers
     * @throws IOException
     */
    @Test
    public void testViewWithOneLoadedFileHeader() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=smallstardata.csv&header=true");

        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("viewcsv");

        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<List<String>> csvData = new ArrayList<>();
        String csvString = "StarID,ProperName,X,Y,Z\n" +
                "0,Sol,0,0,0\n" +
                "1,Andreas,282.43485,0.00449,5.36884\n" +
                "2,Rory,43.04329,0.00285,-15.24144\n" +
                "3,Mortimer,277.11358,0.02422,223.27753\n" +
                "4,Bailee,79.62896,0.01164,-101.53103\n" +
                "5,Zita,264.58918,0.04601,-226.71007\n" +
                "6,Araceli,53.06535,0.0168,3.66089\n" +
                "7,Casey,52.95794,0.02084,19.31343\n" +
                "8,Eura,174.01562,0.08288,84.44669\n" +
                "9,Aracely,166.9363,0.10297,123.9143\n" +
                "10,Destany,58.65441,0.03711,-72.08957\n";
        String[] lines = csvString.split("\n");
        for (int i = 0; i < lines.length; i++) {
            List<String> row = Arrays.asList(lines[i].split(","));
            csvData.add(row);
        }


        assertEquals("success", resp.get("result"));
        assertEquals(csvData, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * test view when switching through multiple loaded files
     * @throws IOException
     */
    @Test
    public void testViewWithDifferentLoadFiles() throws IOException {
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");
        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("loadcsv?filepath=smallstardata.csv&header=true");
        assertEquals(200, clientConnection.getResponseCode());

        clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<List<String>> csvData = new ArrayList<>();
        String csvString = "StarID,ProperName,X,Y,Z\n" +
                "0,Sol,0,0,0\n" +
                "1,Andreas,282.43485,0.00449,5.36884\n" +
                "2,Rory,43.04329,0.00285,-15.24144\n" +
                "3,Mortimer,277.11358,0.02422,223.27753\n" +
                "4,Bailee,79.62896,0.01164,-101.53103\n" +
                "5,Zita,264.58918,0.04601,-226.71007\n" +
                "6,Araceli,53.06535,0.0168,3.66089\n" +
                "7,Casey,52.95794,0.02084,19.31343\n" +
                "8,Eura,174.01562,0.08288,84.44669\n" +
                "9,Aracely,166.9363,0.10297,123.9143\n" +
                "10,Destany,58.65441,0.03711,-72.08957\n";
        String[] lines = csvString.split("\n");
        for (int i = 0; i < lines.length; i++) {
            List<String> row = Arrays.asList(lines[i].split(","));
            csvData.add(row);
        }


        assertEquals("success", resp.get("result"));
        assertEquals(csvData, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }


    /**
     * testing multiple calls to endpoints
     * load, search, view
     * @throws IOException
     */
    @Test
    public void testLoadThenSearchThenView() throws IOException {
        //load
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");
        assertEquals(200, clientConnection.getResponseCode());

        //search
        clientConnection = tryRequest("searchcsv?value=Mortimer");
        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));


        // view
        clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());

        resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

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


        assertEquals("success", resp.get("result"));
        assertEquals(csvData, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }


    /**
     * testing multiple calls to endpoints
     * load, view, then search
     * @throws IOException
     */
    @Test
    public void testLoadThenViewthenSearch() throws IOException {
        //load
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=smallstardata.csv&header=true");
        assertEquals(200, clientConnection.getResponseCode());


        // view
        clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<List<String>> csvData = new ArrayList<>();
        String csvString = "StarID,ProperName,X,Y,Z\n" +
                "0,Sol,0,0,0\n" +
                "1,Andreas,282.43485,0.00449,5.36884\n" +
                "2,Rory,43.04329,0.00285,-15.24144\n" +
                "3,Mortimer,277.11358,0.02422,223.27753\n" +
                "4,Bailee,79.62896,0.01164,-101.53103\n" +
                "5,Zita,264.58918,0.04601,-226.71007\n" +
                "6,Araceli,53.06535,0.0168,3.66089\n" +
                "7,Casey,52.95794,0.02084,19.31343\n" +
                "8,Eura,174.01562,0.08288,84.44669\n" +
                "9,Aracely,166.9363,0.10297,123.9143\n" +
                "10,Destany,58.65441,0.03711,-72.08957\n";
        String[] lines = csvString.split("\n");
        for (int i = 0; i < lines.length; i++) {
            List<String> row = Arrays.asList(lines[i].split(","));
            csvData.add(row);
        }


        assertEquals("success", resp.get("result"));
        assertEquals(csvData, resp.get("data"));


        //search
        clientConnection = tryRequest("searchcsv?value=Mortimer");
        assertEquals(200, clientConnection.getResponseCode());

        resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));

        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }

    /**
     * testing multiple calls to endpoints
     * testing view when switching loaded files
     * then testing search
     * @throws IOException
     */
    @Test
    public void testViewWithDifferentLoadFilesThenSearch() throws IOException {
        // load
        HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=rand.csv");
        assertEquals(200, clientConnection.getResponseCode());
        // view
        clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());

        Map resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

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


        assertEquals("success", resp.get("result"));
        assertEquals(csvData, resp.get("data"));

        //load diff file


        // load
        clientConnection = tryRequest("loadcsv?filepath=smallstardata.csv&header=true");
        assertEquals(200, clientConnection.getResponseCode());
        // view
        clientConnection = tryRequest("viewcsv");
        assertEquals(200, clientConnection.getResponseCode());

        resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<List<String>> csvData2 = new ArrayList<>();
        String csvString2 = "StarID,ProperName,X,Y,Z\n" +
                "0,Sol,0,0,0\n" +
                "1,Andreas,282.43485,0.00449,5.36884\n" +
                "2,Rory,43.04329,0.00285,-15.24144\n" +
                "3,Mortimer,277.11358,0.02422,223.27753\n" +
                "4,Bailee,79.62896,0.01164,-101.53103\n" +
                "5,Zita,264.58918,0.04601,-226.71007\n" +
                "6,Araceli,53.06535,0.0168,3.66089\n" +
                "7,Casey,52.95794,0.02084,19.31343\n" +
                "8,Eura,174.01562,0.08288,84.44669\n" +
                "9,Aracely,166.9363,0.10297,123.9143\n" +
                "10,Destany,58.65441,0.03711,-72.08957\n";
        String[] lines2 = csvString2.split("\n");
        for (int i = 0; i < lines2.length; i++) {
            List<String> row = Arrays.asList(lines2[i].split(","));
            csvData2.add(row);
        }

        assertEquals("success", resp.get("result"));
        assertEquals(csvData2, resp.get("data"));

        //search
        clientConnection = tryRequest("searchcsv?value=Mortimer&columnID=ProperName");
        assertEquals(200, clientConnection.getResponseCode());

        resp = Serialize.adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        List<String> mortimer = Arrays.asList("3", "Mortimer", "277.11358", "0.02422", "223.27753");

        assertEquals("success", resp.get("result"));
        assertEquals(mortimer, resp.get("data"));


        // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.
        clientConnection.disconnect();
    }








}
