package edu.brown.cs32.examples.sprint3.server.handlers;


import edu.brown.cs32.examples.sprint3.server.utilities.WeatherCache;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherClass.Forecast;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherClass.Gridpoint;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherClass.WeatherResponse;
import java.util.Map;

import edu.brown.cs32.examples.sprint3.server.utilities.Serialize;
import java.util.concurrent.ExecutionException;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.HashMap;


/**
 * Class for handling weather requests.
 */
public class WeatherHandler implements Route {

    //the cache is located here
    public WeatherCache cache;

    //Constructor, initializes the cache.
    public WeatherHandler(WeatherCache cache) {
        this.cache = cache;
    }

    /**
     * Handler method for weather requests. Takes in latitude and longitude as parameters
     * Returns serialized errors upon failure (see: Serialize.error), and a WeatherSuccessResponse upon success.
     * @param request
     * @param response
     * @exception IOException
     * @exception ExecutionException
     * @return serialized success or error output
     */
    @Override
    public Object handle(Request request, Response response)
        throws IOException, ExecutionException {
        try {
            String lat = request.queryParams("lat");
            String lon = request.queryParams("lon");
            HashMap<String, Object> map = new HashMap<>();
            if ((lat == null || lat.isBlank()) && (lon == null || lon.isBlank())) {
                return Serialize.error("error_bad_request",
                    "Please call using 'lat, lon': lat and lon are missing");
            }
            if (lat == null || lat.isBlank()) {
                return Serialize.error("error_bad_request",
                    "Please call using 'lat, lon': lat was missing");
            }
            if (lon == null || lon.isBlank()) {
                return Serialize.error("error_bad_request",
                    "Please call using 'lat, lon': lon was missing");
            }
            String newurl = "https://api.weather.gov/points/" + lat + "," + lon;
            //parsing lat and longitude values here, now that we've called all the errors for isBlank.
            double latt = Double.parseDouble(lat);
            double lonn = Double.parseDouble(lon);
            Gridpoint gridResponse = cache.getForecast(newurl);
            if (latt < -90 || latt > 90 || lonn < -180 || lonn > 180) {
                return Serialize.error("error_bad_request",
                        "Data is now out of bounds, please enter a latitude between -90 and 90, and a longitude"
                            + " between -180 and 180.");
            }
            if (gridResponse != null) { //checking if gridResponse returned properly
                if(gridResponse.properties() != null) { //checking if its properties are valid (any site in the API will have similarly formatted properties)
                    String weatherUrl = gridResponse.properties().endpoint();
                    WeatherResponse weatherResponse = this.cache.getWeatherResponse(latt, lonn, weatherUrl); //making coord class and calling the cache here
                    if(weatherResponse == null) {
                        if (weatherResponse.properties() == null) {
                            return Serialize.error("error_bad_request",
                                "Unable to get properties from point at coordinates " + lat + "," + lon + ".");
                        }
                        return Serialize.error("error_bad_request", "Failed to create"
                            + " weather response from weather.", map);
                    }
                    Forecast forecast = weatherResponse.properties().periods().get(0); //using weatherResponse to format final successful response!
                    HashMap<String, Object> map2 = new HashMap<>();
                    map2.put("Latitude", latt);
                    map2.put("Longitude", lonn);
                    map2.put("Temperature", forecast.temp());
                    map2.put("Unit", forecast.unit());
                    map2.put("Date and Time Retrieved", cache.getCurrentTime());
                    return Serialize.success(map2);
                }
                else {
                        return Serialize.error("error_bad_request",
                            "Grid response was found, but didn't have properties, at point " + lat + "," + lon + ".");
                }

            } else {
                    return Serialize.error("error_bad_request", "Grid response returned null-- there is no data for the point " + lat + "," + lon + ".");
            }
        }catch(NumberFormatException e)
        {
            return Serialize.error("error_bad_request",
                "Number Format was incorrect.", new HashMap<>());
        }
        catch(Exception e){ //this should never throw during normal functionality, but exists as a
            //developer precaution.
            e.printStackTrace();
            return Serialize.error("error_bad_request", "Unknown error.", new HashMap<>());
        }
}

    /**
     * The object which handle returns upon success. Parameters are all the data needed for a successful
     * final output, as will have already been retrieved from the Weather Response. This just fills the final map
     * for display.
     * @param lat
     * @param lon
     * @param temp
     * @param units
     * @param dateTime
     * @return
     */


}
















