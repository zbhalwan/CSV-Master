package edu.brown.cs32.examples.sprint3.server.handlers;

import edu.brown.cs32.examples.sprint3.server.utilities.DataStorage;
import edu.brown.cs32.examples.sprint3.server.utilities.Serialize;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.List;

/**
 * class that allows loaded csv file to be viewed
 */

public class ViewHandler implements Route {
    private List<List<String>> list;
    private LoadHandler loadHandler;

    private HashMap<String, Object> map = new HashMap<>();

    public ViewHandler(LoadHandler loadHandler) {
        this.loadHandler = loadHandler;
    }

    /**
     * method that handles view functionality for csv. if file has been loaded, successfully serializes CSV as a list of
     * list of strings else responds with error that no file was loaded
     * @param request
     * @param response
     * @return successfully serializes CSV as a list of list of strings else responds with error that no file was loaded
     */
    @Override
    public Object handle(Request request, Response response) {
        this.list = DataStorage.getCurrentData();
        if(this.list != null){
            map.put("data", this.list);
            return Serialize.success(map);
//            return viewSuccessResponse(this.list);
        }
        else{
            return Serialize.error("error_datasource", "No file loaded. Load file using loadcsv endpoint and query parameter 'filename'.");
        }
    }

}
