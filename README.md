# CSV-Master

## Contributors
Developed by Zeeshan Bhalwani (zbhalwan) & Kyle Sohn (ksohn3)

## Project Description
Integrating the front- and back-end components previously been developing separately (Echo and Server). Majority of development was done on the front-end, and will invoke the API server you completed in Sprint 3. Converted previous webapp (Echo) to use the React framework.

## Usage

There are four valid csv files that are available to use through the following file paths:

* `data` package in the `backend` directory includes csv files.
    * `rand.csv` small csv file with no headers
    * `smallstardata.csv` small csv file with headers
    * `ten-star.csv` small csv file with headers
    * `stardata.csv` large csv file
####
And the following commands:
1. `mode brief` and `mode verbose`: toggle between brief and verbose mode; starts as brief
2. `load_file <filepath>`: valid for one of the filepaths above. loads the file into the program state.
3. `view`: view loaded CSV file as a table
4. `search <desired value> <column index or header>`: search value in table, where column can be an int for a column index (zero-indexed) or a column header.

Shortcuts:
  * Tab over to the command box and use the following:
    * `ctrl + [` to populate input box with `load_file`
    * `ctrl + b` to populate input box with `mode brief`
    * `ctrl + v` to populate input box with `mode verbose`
    * `ctrl + .` to populate input box with `view`
    * `ctrl + s` to populate input box with `search`
    * `enter` to press submit button


To add a **new command**, the developer can create a new file in the `api` defining the functionality for the command. The developer can then add the command to the registry by using the `registerCommand()` function. The developer can remove that same command by using the `removeCommand()` function.

## Design Choices

In the project repo is `backend` and `frontend`:

* `backend` contains code for API server navigate to backend directory and see README there for more information.

* `frontend` contains Reactified frontend code from Sprint 2 to display user friendly results from our backend web api server
  * `src` direcotry contains `api` and `components` directories and `App.tsx` and `main.tsx`
    * `api` directory
      * `datatypes` directory contains classes to store response, command and csv data
      * `API.ts` class that contains all the functions that are used to interact with the backend
      * `modeHandler.tsx` handles everything to do with switching between modes verbose and brief
      * `REPLFunction.tsx` stores a function that takes in an array of strings and returns a promise of a CommandResponse
    * `components` directory contains the code for the various parts of the webpage such as the input box, history box, header, and table
    * `App.tsx` contains component that is rendered in the index.tsx file.
    * `Main.tsx` entry point that React uses to render the app.

  * `styles` directory contains styling in CSS for the webapp
  * `tests` contains the DOM and integration testing for the webapp 



## Tests
We have 3 testing files in the frontend --> src --> tests directory:
  * `integration.dom.test.tsx`
    * contains dom integration tests
  * `main.unit.test.tsx`
    * contains unit tests
  * `mocks.dom.tsx`
    * consists of tests using mocked api and command resposnes

When you run the tests, you have to make sure that the server is first called by running `Server.Java`. Once you run this, cd into the `frontend` directory you can now run `npm test` in the terminal. `mocks.dom.tsx` doesn't require `Server.java` to be run in advance while the other 2 testing files do. All of the tests should pass.In our testing, we have three different testing classes. All of the names of the classes are appropriate to the tests that are contain inside of them. In our `integration.dom.test`, we have integration and dom tests, which is utilizing the dom package library as well as integration tests, connecting the front and back end of our code. In our mock tests, we are using mocked data. They are all pretty self explanatory.



## Errors and Bugs
There are many different errors returned by the API, so documenting them all would entail lot of time for the project. No bugs were indetified. The following errors are outputted in a user-friendly and understanding manner on our webapp. Generally, these errors are returned:
* `error_bad_request`: if the request was ill-formed or one of its fields was ill-formed.
* `error_datasource`: if the given data source wasn't accessible (e.g., the file didn't exist or the NWS API returned an error for a given location).
* `error_internal`: an unexpected system error, that ideally should be logged and returned to the developers to check upon.
* `error_bad_json`: if the request was ill-formed

## Running the Program
* cd into backend --> src --> main --> server and run `Server.java`
  * go to `localhost:3232` and utilize API endpoints and parameters detailed in the API section of README in backend directory.
* cd into frontend and run first `npm install` and then `npm start` or `npm run dev`
* Run the tests: cd into `frontend` directory and run `Server.java` and then `npm test`