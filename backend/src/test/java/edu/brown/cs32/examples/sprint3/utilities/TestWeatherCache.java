package edu.brown.cs32.examples.sprint3.utilities;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.brown.cs32.examples.sprint3.server.utilities.WeatherCache;
import edu.brown.cs32.examples.sprint3.server.utilities.Coordinate;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestWeatherCache {

  private HashMap<Coordinate, String> weatherMockMap;
  private WeatherCache cache;

  private Coordinate coord1 = new Coordinate(43.384, -93.548, "https://api.weather.gov/gridpoints/DMX/74,131/forecast");
  private Coordinate coord2 = new Coordinate(36, -112, "https://api.weather.gov/gridpoints/FGZ/65,125/forecast");
  private Coordinate coord3 = new Coordinate(36.9, -112.9, "https://api.weather.gov/gridpoints/FGZ/65,125/forecast");
  private Coordinate coord4 = new Coordinate(36.001, -112.001, "https://api.weather.gov/gridpoints/FGZ/65,125/forecast");

  @BeforeEach
  public void setup() throws MalformedURLException {

    weatherMockMap = new HashMap<>();
    weatherMockMap.put(new Coordinate(36.5, -92.345, "https://api.weather.gov/gridpoints/SGF/100,3/forecast"),
        """ 
            {
            "properties": {
                "periods": [
                  {
                    "number": 1,
                    "name": "Overnight",
                    "startTime": "2023-03-03T04:00:00-06:00",
                    "endTime": "2023-03-03T06:00:00-06:00",
                    "isDaytime": false,
                    "temperature": 44,
                    "temperatureUnit": "F",
                    "temperatureTrend": null,
                    },
                    ]
                    },  
                    }                      
                """
    );
    weatherMockMap.put(new Coordinate(43.384, -93.548, "https://api.weather.gov/points/43.384,-93.548"),
        """ 
            {
            "properties": {
                "periods": [
                  {
            "periods": [
            {
                   "number": 1,
                   "name": "Overnight",
                   "startTime": "2023-03-03T04:00:00-06:00",
                   "endTime": "2023-03-03T06:00:00-06:00",
                   "isDaytime": false,
                   "temperature": 23,
                   "temperatureUnit": "F",
                    },
                    ]
                    },  
                    }                      
                """
    );
    weatherMockMap.put(new Coordinate(43.385, -93.549, "https://api.weather.gov/points/43.385,-93.549"),
        """ 
            {
            "properties": {
                "periods": [
                  {
            "periods": [
            {
                   "number": 1,
                   "name": "Overnight",
                   "startTime": "2023-03-03T04:00:00-06:00",
                   "endTime": "2023-03-03T06:00:00-06:00",
                   "isDaytime": false,
                   "temperature": 23,
                   "temperatureUnit": "F",
                    },
                    ]
                    },  
                    }                      
                """
    );
  }

  /**
   * Tests simple load
   */
  @Test
  public void cacheLoadTest() throws IOException, ExecutionException {
    this.cache = new WeatherCache();
    this.cache.getWeatherResponse(coord1.getLat(), coord1.getLon(), coord1.getURL());
    //checks cache load success count
    assertEquals(1, this.cache.cache.size());
    //check that getWeatherResponse of the same value twice doesn't cache it again
    this.cache.getWeatherResponse(coord1.getLat(), coord1.getLon(), coord1.getURL());
    assertEquals(1, this.cache.cache.size());
    //check the cache was "looked at" this time
    assertEquals(1, this.cache.cache.stats().hitCount());
    //close but not close enough for cacheing...
    this.cache.getWeatherResponse(coord2.getLat(), coord2.getLon(), coord2.getURL());
    this.cache.getWeatherResponse(coord3.getLat(), coord3.getLon(), coord3.getURL());
    //now, check that the size of the cache has gone up by 2
    assertEquals(3, this.cache.cache.size());
    //check functionality of "small enough" to trigger load from cache
    this.cache.getWeatherResponse(coord4.getLat(), coord4.getLon(), coord4.getURL());
    //cache size shouldn't change
    assertEquals(3, this.cache.cache.size());
    //check that cache was checked again
    assertEquals(2, this.cache.cache.stats().hitCount());
  }


}