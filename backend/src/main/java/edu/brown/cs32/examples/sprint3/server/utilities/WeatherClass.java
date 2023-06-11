package edu.brown.cs32.examples.sprint3.server.utilities;

import com.squareup.moshi.Json;
import java.util.List;

/**
 * Class containing public classes related to weather functionality.
 * Creating records gives quick getters/setters and data carriage.
 */
public class WeatherClass {

    public record Forecast(@Json(name = "temperature") double temp, @Json(name = "temperatureUnit") String unit) {}
    public record ForecastProperties(@Json(name = "periods") List<Forecast> periods) {}
    public record WeatherResponse(@Json(name = "properties") ForecastProperties properties) {}
    public record Gridpoint(@Json(name = "properties") Gridpieces properties, @Json(name = "title") String title) {}
    public record Gridpieces(@Json(name = "forecast") String endpoint) {}
}



