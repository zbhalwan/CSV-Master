package edu.brown.cs32.examples.sprint3.server.handlers;

import edu.brown.cs32.examples.sprint3.csv.src.main.java.Searching.Search;
import edu.brown.cs32.examples.sprint3.server.utilities.DataStorage;
import edu.brown.cs32.examples.sprint3.server.utilities.Serialize;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.List;

/**
 * class that allows a loaded csv file to be searched for a desired value
 */
public class SearchHandler implements Route {

    private List list;
    private LoadHandler loadHandler;
    public SearchHandler (LoadHandler loadHandler) {
        this.loadHandler = loadHandler;
    }

    /**
     * method that handles searching for desired value.
     * checks if desired value and list are not null so it can search for the value. if there is no column index/header
     * and if the value is not found, return error. else return successful row with desired value. if a column ID is passed
     * in, check whther its a number. If a number, then search for the desired value. Error check to make sure the index isn't
     * out of bounds. Catch NumberFormatException if colID can't be parsed as an integer. Search for desired value based on header
     * and respond with error if column doesn't contain desired value or CSV doesn't have headers.
     * 
     * Return as 2d array of strings
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object handle(Request request, Response response) {
        String value = request.queryParams("value");
        String colID = request.queryParams("columnID");
        this.list = DataStorage.copyCurrentData();
        HashMap<String, Object> map = new HashMap<>();


        Search s = new Search(this.loadHandler.getParser());

        if(value != null && this.list != null) {

            if (colID == null) {
                s.search(this.list, value, colID);
                if (!s.isValueFound()) {
                    return Serialize.error("error_bad_request","desired value -" + value + "- was not found.");
                }
                else {
                    map.put("query", value);
                    map.put("data", List.of(s.search(this.list, value, colID)));
                    return Serialize.success(map);
//                    return searchSuccessResponse(s.search(this.list, value, colID));
                }
            }

            else if (colID != null) {
                try {
                    int index = Integer.parseInt(colID);
                    if (index > (DataStorage.getCurrentData().get(0).size()-1)) {
                        return Serialize.error("error_bad_request","The inputted column number -" + colID + "- is out of bounds. There are " + String.valueOf(DataStorage.getCurrentData().get(0).size()) + " columns. column index is zero-indexed.");
                }
                    // if value not in inputted col index
                    else if(s.search(this.list, value, colID) == null) {
                        return Serialize.error("error_bad_request","The inputted column number -" + colID + "- " +
                                "does not contain desired value. Use a different column index or simply just pass in the desired value without a column identifier.");
                    }
                    else {
                        map.put("query", value);
                        map.put("columnID", colID);
                        map.put("data", List.of(s.search(this.list, value, colID)));
                        return Serialize.success(map);
//                        return searchSuccessResponse(s.search(this.list, value, colID));
                    }
                } catch (NumberFormatException e) {
                    if (DataStorage.isHeader()) {
                        if (DataStorage.getCurrentData().get(0).contains(colID) && s.search(this.list, value, colID) != null) {
                            map.put("query", value);
                            map.put("columnID", colID);
                            map.put("data", List.of(s.search(this.list, value, colID)));
                            return Serialize.success(map);
//                            return searchSuccessResponse(s.search(this.list, value, colID));
                        }
                        else { return Serialize.error("error_bad_request","The column " + colID + " does not contain the desired value. Here is a list of present column headers: " + DataStorage.copyCurrentData().get(0).toString()); }
                    }
                    else {
                        return Serialize.error("error_bad_request","CSV has no headers, use column index or simply just pass in the desired value without a column identifier.");
                    }
                }
            }
        }

        else if (this.list == null) {
            return Serialize.error("error_datasource","No file loaded. Load file using loadcsv endpoint and query parameter 'filename'.");
        }
        else if(value == null) {
            return Serialize.error("error_bad_request","Please specify desired value");
        }

        else {
            return Serialize.error("error_bad_json", "JSON conversion failed. Check server.");
        }
        return null;
    }

    /**
     * success method for successful serializing
     * @param data data list of parsed csv
     * @return Serialized string
     */


}

