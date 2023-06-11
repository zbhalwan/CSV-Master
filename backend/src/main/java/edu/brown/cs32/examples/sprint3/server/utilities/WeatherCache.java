package edu.brown.cs32.examples.sprint3.server.utilities;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.squareup.moshi.Moshi;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherClass.Gridpoint;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherClass.WeatherResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;


/**
 * The class for the cache.
 */
public class WeatherCache {

  //Used to control maximum cache size.
  private final int MAX_SIZE = 10;
  //Used to control time until cache eviction.
  private final int EXP_MINUTES = 60;

  //Used to control distance between points deemed "close enough".
  private final int MIN_DISTANCE = 1;

  //Loading cache.
  public final LoadingCache<Coordinate, WeatherResponse> cache;

  /**
   * Class declaration initializes the cache. Cache is built and contains override for load, which calls
   * custom weather response "getter".
   */
  public WeatherCache(){
    cache = CacheBuilder.newBuilder()
        .maximumSize(MAX_SIZE)
        .expireAfterWrite(EXP_MINUTES, TimeUnit.MINUTES)
        .recordStats() //keep stats for testing
        .build(new CacheLoader<>() {
          @Override
          @NotNull
          public WeatherResponse load(Coordinate key) throws Exception {
            return getWeatherResponse(key.getLat(), key.getLon(), key.getURL());
          }
        });
    }

  /**
   * Weather response retriever. Returns a weather response, takes in latitude, longitude,
   * and URL. These fields are all provided by either the WeatherHandler or the Coordinate.
   * The cache is converted to a map and its keys are iterated through. If a coordinate is
   * close enough, it is retrieved from the cache. If no "close enough" value exists in the
   * cache, a WeatherResponse is instantiated using the URL.
   * @param lat
   * @param lon
   * @param URL
   * @return A WeatherResponse, either from cache or from instantiation.
   * @throws ExecutionException
   * @throws IOException
   */
  public WeatherResponse getWeatherResponse(double lat, double lon, String URL)
      throws ExecutionException, IOException {
    for(Coordinate c : this.cache.asMap().keySet()) {
      if (Math.sqrt(Math.pow(lat - c.getLat(), 2) + Math.pow(lon - c.getLon(), 2)) < MIN_DISTANCE) {
        return this.cache.getUnchecked(c);
      }
    }
    WeatherResponse result = instantiate(URL, WeatherResponse.class);
    cache.put(new Coordinate(lat, lon, URL), result);
    return result;
  }


  /**
   * Used to get the data for a given gridpoint. Called by WeatherHandler
   * to get the grid from initial coordinates.
   * @param url
   * @return Gridpoint class.
   * @throws IOException
   */
  public Gridpoint getForecast(String url) throws IOException {
    return instantiate(url, Gridpoint.class);
  }

  /**
   * Instantiate creates a new class from a URL and a JSON, by connecting to a
   * website and using moshi to build a JSON.
   * @param url to gather JSON data from.
   * @param targetClass class to be adapted into.
   * @return class of type T.
   * @param <T>
   * @throws IOException
   */
  public static <T> T instantiate(String url, Class<T> targetClass) throws IOException {
      System.out.println(url);
      URL requestUrl = new URL(url);
      HttpURLConnection clientConnection = (HttpURLConnection) requestUrl.openConnection();
      clientConnection.connect();
      T response = null;
      int responseCode = clientConnection.getResponseCode();
      System.out.println(responseCode);
      //for this code, we WANT to respond positively even with a 404... because weatherAPI
      //THROWS 404 AS THE STATUS for pages where the point isn't found!
      if (responseCode == 200) {
        Moshi moshi = new Moshi.Builder().build();
        response =
            moshi
                .adapter(targetClass)
                .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      }
      clientConnection.disconnect();
      return response;
  }


  /**
   * Helper method for getting the time.
   * @return string formatted time.
   */
  public String getCurrentTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    return dtf.format(now);
  }

}
