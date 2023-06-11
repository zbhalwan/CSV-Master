package edu.brown.cs32.examples.sprint3.server.utilities;

/**
 * Coordinate class for support with caching, containing the latitude, longitude, and URL.
 */
public class Coordinate {
  private double lat;
  private double lon;

  private String url;

  /**
   * Constructor
   * @param lat: the latitude, represented by a double
   * @param lon: the longitude, represented by a double
   * @param url: the URL, represented by a string
   */
  public Coordinate(double lat, double lon, String url) {
    this.lat = lat;
    this.lon = lon;
    this.url = url;
  }
  /**
   * Getters for latitude, longitude, and URL
   */
  public double getLat() {return lat;}

  public double getLon() {return lon;}

  public String getURL() {return url;}

}