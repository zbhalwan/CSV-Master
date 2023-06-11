package edu.brown.cs32.examples.sprint3.utilities;

import edu.brown.cs32.examples.sprint3.server.utilities.WeatherClass;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * tests for WeatherClass which contains records related to weather functionality
 */
public class TestWeatherClass {
    /**
     * testing forecast record
     */
    @Test
    public void testForecast() {
        WeatherClass.Forecast forecast = new WeatherClass.Forecast(72.5, "F");
        assertEquals(72.5, forecast.temp(), 0.001);
        assertEquals("F", forecast.unit());
    }
    /**
     * testing forecast properties record
     */
    @Test
    public void testForecastProperties() {
        WeatherClass.Forecast forecast1 = new WeatherClass.Forecast(72.5, "F");
        WeatherClass.Forecast forecast2 = new WeatherClass.Forecast(25.0, "C");
        List<WeatherClass.Forecast> periods = Arrays.asList(forecast1, forecast2);
        WeatherClass.ForecastProperties forecastProperties = new WeatherClass.ForecastProperties(periods);
        assertEquals(periods, forecastProperties.periods());
    }
    /**
     * testing weather response record
     */
    @Test
    public void testWeatherResponse() {
        WeatherClass.Forecast forecast1 = new WeatherClass.Forecast(72.5, "F");
        WeatherClass.Forecast forecast2 = new WeatherClass.Forecast(25.0, "C");
        List<WeatherClass.Forecast> periods = Arrays.asList(forecast1, forecast2);
        WeatherClass.ForecastProperties forecastProperties = new WeatherClass.ForecastProperties(periods);
        WeatherClass.WeatherResponse weatherResponse = new WeatherClass.WeatherResponse(forecastProperties);
        assertEquals(forecastProperties, weatherResponse.properties());
    }
    /**
     * testing GridPoint and GridPieces record
     */
    @Test
    public void testGridpointAndGridPieces() {
        WeatherClass.Gridpieces gridpieces = new WeatherClass.Gridpieces("https://weather.api/endpoint");
        WeatherClass.Gridpoint gridpoint = new WeatherClass.Gridpoint(gridpieces, "Temperature");
        assertEquals(gridpieces, gridpoint.properties());
        assertEquals("Temperature", gridpoint.title());
    }



}
