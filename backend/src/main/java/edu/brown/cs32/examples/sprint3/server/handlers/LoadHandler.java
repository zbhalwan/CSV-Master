package edu.brown.cs32.examples.sprint3.server.handlers;

import edu.brown.cs32.examples.sprint3.csv.src.main.java.Exceptions.FactoryFailureException;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing.CSVParser;
import edu.brown.cs32.examples.sprint3.csv.src.main.java.Parsing.CreateList;
import edu.brown.cs32.examples.sprint3.server.utilities.DataStorage;
import edu.brown.cs32.examples.sprint3.server.utilities.Serialize;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * class that loads csv file so it can be searched or viewed
 */
public class LoadHandler implements Route {
    private CSVParser parser;

    /**
     * handle method that allows for file to be loaded. takes in a relative filepath and whether the csv has headers or
     * not. if the file has headers, true is added to the map else false by default. The CSVParser class is called to parse the inputted
     * filepath. If there are no errors, there we be a serialized success output with the filepath being loaded. Otherwise,
     * exceptions will caught and serialized error messages will be outputted. Exceptions being checked for are FactoryFailureException,
     * FileNotFoundException, and IOException.
     * @param request
     * @param response
     * @return serialized success or error output
     */
    @Override
    public Object handle(Request request, Response response) {
        String fileName = request.queryParams("filepath");

        HashMap<String, Object> map = new HashMap<>();
        map.put("filepath", fileName);

        if (fileName == null || fileName.isBlank()) {
            return Serialize.error("error_bad_request", "filepath needed", map);
        }

        String file = "backend/src/main/java/edu/brown/cs32/examples/sprint3/data/" + fileName;

        try (FileReader reader = new FileReader(file)) {

            String header = request.queryParams("header");
            this.parser = new CSVParser(reader, header != null && header.equalsIgnoreCase("true"), new CreateList());
            if (this.parser.getHasHeaders()) {
                map.put("header", "true");
            }
            else { map.put("header", "false"); }

            DataStorage.setData(this.parser.getData()); // parser.getData() creates a deep copy of the data in parser class
            DataStorage.setHeader(this.parser.getHasHeaders());
            return Serialize.success(map);
        }

        catch (FactoryFailureException e) {
            return Serialize.error("error_bad_json",
                    "Parsing error on row: " + e.row.toString() + ".");
        }
        catch (FileNotFoundException e) {
            return Serialize.error("error_datasource", "The file " + fileName +" not found.");
        }
        catch (IOException e) {
            return Serialize.error("error_internal", "Unexpected I/O error.");
        }
    }

    /**
     * gerrer method that gets the parser being used to load file
     * @return parser being used to laad file
     */
    public CSVParser getParser() {
        return this.parser;
    }




}


