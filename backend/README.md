# cs32 --> REPL Backend from Sprint 3 **Server!**
## Contributors
Developed by Zeeshan Bhalwani `zbhalwan` & Jack Cobey `kcobey`

Total time: 20 hours

Original Github Repo: https://github.com/cs0320-s2023/sprint-3-kcobey-zbhalwan.git

Current Github Repo: https://github.com/cs0320-s2023/sprint-4-ksohn3-zbhalwan

Would like to acknowledge mke2, pramach3, aagvania, jkdai for their code which we took some inspiration from.

## Project Description
Used Java backend code from Sprint 1 to make CSV parser and searcher available to others via a web API. Also added a 2nd data source: weather requests, which processes and resolves via the U.S. National Weather Service's own web API. Developed server that responds to requests of a much simpler form than the NWS API does.

## Design Choices

In the `src` package is `main` and `test`:


* `main` project entry point.
    * `server` includes server handling code for csv and weather
        * `handlers` contains csv and weather handlers
            * `LoadHandler.java` allows for file to be loaded. takes in a relative filepath and whether the csv has headers or not. if the file has headers, true is added to the map else false by default The CSVParser class is called to parse the inputted filepath. If there are no errors, there we be a serialized success output with the filepath being loaded. Otherwise, exceptions will caught and serialized error messages will be outputted. Exceptions being checked for are FactoryFailureException, FileNotFoundException, and IOException.
            * `SearchHandler.java` handles searching for desired value. checks if desired value and list are not null so it can search for the value. if there is no column index/header and if the value is not found, return error. else return successful row with desired value. if a column ID is passed in, check whther its a number. If a number, then search for the desired value. Error check to make sure the index isn't out of bounds. Catch NumberFormatException if colID can't be parsed as an integer. Search for desired value based on header and respond with error if column doesn't contain desired value or CSV doesn't have headers.

            * `ViewHandler.java` handles view functionality for csv. if file has been loaded, successfully serializes CSV as a list of list of strings else responds with error that no file was loaded

            * `WeatherHandler.java` handles weather requests. Takes in latitude and longitude as parameters Returns serialized errors upon failure (see: Serialize.error), and a WeatherSuccessResponse upon success.
        * `utilities` contains utilities used for handling api endpoints
            * `DataStorage.java` stores data globally for csv API
            * `Serialize.java` serialzies success and error API events
            * `Unserialize.java` unserializes JSON
            * `WeatherCache.java` Cache is built and contains override for load, which calls custom weather response "getter". The getter returns a weather response, takes in latitude, longitude, and URL. These fields are all provided by either the WeatherHandler or the Coordinate. The cache is converted to a map and its keys are iterated through. If a coordinate is close enough, it is retrieved from the cache. If no "close enough" value exists in the cache, a WeatherResponse is instantiated using the URL.
            * `WeatherClass.Java` Class containing public classes related to weather functionality. Creating records gives quick getters/setters and data carriage.
    * `csv` contains code from sprint 1
    * `data` package includes csv files.
        * `rand.csv` small csv file with no headers
        * `smallstardata.csv` small csv file with headers
        * `ten-star.csv` small csv file with headers
        * `stardata.csv` large csv file
* `test` contains unit and integration testing
    * integration tests for each of the handlers in the `handlers` subdirectory
        * `TestLoadCSVHandler.java`
        * `TestSearchCSVHandler.java`
        * `TestViewCSVHandler.java`
    * unit tests for the different utilities in the `utilities` subdirectory
        * `TestDataStorage.java`
        * `TestSerializeAndUnserialze.java`
        * `TestWeatherCache.java`
        * `TestWeatherClass.java`

## Tests
Tests are split according to the previous package hierarchy. Test names are mostly self-explanatory, so less documentation was given there. Integration tests are performed for each of the handlers. Unit tests are performed for utilites. To run the tests, run the test suite.

## API Endpoints
* `loadcsv`: opens the file on the server. Accepts mandatory `filepath` field, which indicates which file to open, optionally loads headers separately if `header` is set to `true`. `header` is set to false by default.
    * example: `localhost:3232/loadcsv?filepath=stardata.csv&header=true`
* `viewcsv`: returns the currently loaded file.
    * example: `localhost:3232/viewcsv`
* `searchcsv`: searches the currently open file. Accepts mandatory `value` field, which indicates the desired value being searched for. Also accepts an option `columnID` parameter which can be either the column index (zero-indexed) or the column header if the csv contains headers.
    * example: `localhost:3232/searchcsv?value=Andreas&columnID=1`
    * example: `localhost:3232/searchcsv?value=Andreas&columnID=ProperName`
* `weather`: requests the weather from a given point, proxies the request to NWS. Accepts to mandatory parameters, `lat` and `lon`, which should be valid latitude and longitude respectively.
    * example: `localhost:3232/weather?lat=41.8240&lon=-71.4128`

## Errors and Bugs
There are many different errors returned by the API, so documenting them all would entail lot of time for the project. No bugs were indetified. Generally, these errors are returned:
* `error_bad_request`: if the request was ill-formed or one of its fields was ill-formed.
* `error_datasource`: if the given data source wasn't accessible (e.g., the file didn't exist or the NWS API returned an error for a given location).
* `error_internal`: an unexpected system error, that ideally should be logged and returned to the developers to check upon.
* `error_bad_json`: if the request was ill-formed

## Running the Program
* Run `Server.main()` and go to `localhost:3232` and utilize API endpoints and parameters detailed in the API section.
* Run the tests: `mvn test`
* Create a .jar file: `mvn package`
* Generate coverage (and other) info: `mvn site`