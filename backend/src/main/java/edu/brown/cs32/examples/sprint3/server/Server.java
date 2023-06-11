package edu.brown.cs32.examples.sprint3.server;

import static spark.Spark.after;

import edu.brown.cs32.examples.sprint3.server.handlers.LoadHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.SearchHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.ViewHandler;
import edu.brown.cs32.examples.sprint3.server.handlers.WeatherHandler;
import edu.brown.cs32.examples.sprint3.server.utilities.WeatherCache;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the various handlers.
 *
 * We have two endpoints in this demo. They need to share state (a menu).
 * This is a great chance to use dependency injection, as we do here with the menu set. If we needed more endpoints,
 * more functionality classes, etc. we could make sure they all had the same shared state.
 */
public class Server {
    public static void main(String[] args) {


        Spark.port(3232);
        /*
            Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
            be able to make requests to the server.

            By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
            This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
            from any website. Instead, you should set this header to the origin of your client, or a list of origins
            that you trust.

            By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
            Again, it's generally better to be more specific here and only allow the methods you need, but for
            this demo we'll allow all methods.

            We recommend you learn more about CORS with these resources:
                - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
                - https://portswigger.net/web-security/cors
         */
        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "*");
        });

        // Setting up the handler for the GET /order endpoint
//        Spark.get("order", new OrderHandler(menu));

        LoadHandler loadHandler = new LoadHandler();
        WeatherCache cache = new WeatherCache();
        WeatherHandler weatherHandler = new WeatherHandler(cache);

        Spark.get("loadcsv", loadHandler);
        Spark.get("weather", weatherHandler);
        Spark.get("searchcsv", new SearchHandler(loadHandler));
        Spark.get("viewcsv", new ViewHandler(loadHandler));
        Spark.init();
        Spark.awaitInitialization();
        System.out.println("Server started.");
    }
}
