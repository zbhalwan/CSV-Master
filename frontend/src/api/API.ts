import { REPLFunction } from "./REPLFunction";
import { CommandResponse } from "./datatypes/CommandResponse";
import { APIResponse } from "./datatypes/APIResponse";
import { Mode } from "./datatypes/ModeData";

/**
 * class that contains all the functions that are used to interact with the backend
 */

let loadedHasHeader = false;
let currentCSV = "";

function clear() {
  loadedHasHeader = false
  currentCSV = "";
}

function getDefaultItem(args: string[]): CommandResponse {
    return {
      command: args.join(" "),
      output: "",
      data: undefined,
      hasHeader: loadedHasHeader,
    };
  }

    //this is the getAPIResponse function that returns the API in the form of a promise through a URL that is passed in
  
  async function getAPIResponse(url: string): Promise<APIResponse> {
    const response: Response = await fetch(url);
    const responseJson: APIResponse = await response.json();
    return responseJson;
  }
  
    //this is load REPLFunction that allows our load functionality to be synced to the backend part of our code and is dealt with in the register commands part of the InputBox.tsx

export const load: REPLFunction = async (args: string[]) => {
    let history: CommandResponse = getDefaultItem(args);
    let filepath: string = args[1];
    let hasHeader = args[2] === "true" ? "true" : "false";
    
    if (args.length < 2 || args.length > 3) {
      history.output =
        "load_file only accepts a filepath in the format 'load_file <filepath> <headersPresent> (optional)'.";
      return new Promise<CommandResponse>((resolve) => {
        resolve(history);
      });
    }

    //once the error above has been checked, we are able to load the file

    else {
      let response: APIResponse = await getAPIResponse(
        `http://localhost:3232/loadcsv?filepath=${filepath}&header=${hasHeader}`
      );
      return new Promise<CommandResponse>((resolve) => {
        if (response.result === "success") {
          loadedHasHeader = hasHeader === "true" ? true : false;
          currentCSV = filepath;
          history.output = `'${filepath}' successfully loaded.`;
          resolve(history);
        } else if (response.result === "error_datasource") {
          history.output =
            "error: file could not be loaded. Please check the filepath and try again.";
          resolve(history);
        } else {
          history.output = `error: file '${filepath}' could not be loaded. Here is the error: ${response.message}.`;
          resolve(history);
        }
      });
    }
  };

    //this is the view REPLFunction that i connected to the backend view method and is dealt with in the InputBox class within register commands

  export const view: REPLFunction = async (args: string[]) => {
    let history: CommandResponse = getDefaultItem(args);

    //here, we are checking if the correct number of arguments are passed in... in order to view something the user should only type in view, so if anything else is typed it should return an error

    if (args.length > 1) {
      history.output =
        "'view' doesn't take any arguments. Please type 'view' to view the loaded file.";
      return new Promise<CommandResponse>((resolve) => {
        resolve(history);
      });

      //here, we are checking to see if any CSV file has been loaded before the user attempts to view the CSV data

    } else if (currentCSV === "") {
      history.output = "error: no file was loaded.";
      return new Promise<CommandResponse>((resolve) => {
        resolve(history);
      });

      //once the errors above have been covered, we get into the actual view function that is linked to our backend code

    } else {
      let response: APIResponse;
      response = await getAPIResponse(`http://localhost:3232/viewcsv`);
      return new Promise<CommandResponse>((resolve) => {
        if (response.result === "success") {
          history.output =
            "view successful. now displaying loaded file as a table.";
          history.data = response.data;
          resolve(history);
        } else {
          history.output = `error: view unsuccessful. error type: ${response.message}.`;
        }
      });
    }
  };

    //this is the search REPLFunction that connects the frontend of search to the backend part of our code and it is dealt with within the register commands in the InputBox.tsc class

  export const search: REPLFunction = async (args: string[]) => {
    let history: CommandResponse = getDefaultItem(args);

    //this checks if no file has been loaded

    if (currentCSV === "") {
      history.output = "error: no file was loaded.";
      return new Promise<CommandResponse>((resolve) => {
        resolve(history);
      });

      //this makes sure that the correct number of arguments are inputted

    } else if (args.length < 2 || args.length > 3) {
      history.output =
        "search only accepts a query in the format 'search <query> <columnID> (optional)'.";
      return new Promise<CommandResponse>((resolve) => {
        resolve(history);
      });
    } else {
      let response: APIResponse;
      if (args.length === 2) {
        response = await getAPIResponse(
          `http://localhost:3232/searchcsv?value=${args[1]}`
        );

        //after checking the errors above, we now proceed into function where the searching happens

      } else {
        response = await getAPIResponse(
          `http://localhost:3232/searchcsv?value=${args[1]}&columnID=${args[2]}`
        );
      }
      return new Promise<CommandResponse>((resolve) => {
        if (response.result === "success") {
          history.output = "search successful";
          console.log(response.data);
          history.data = response.data;
          resolve(history);
        } else if (response.result === "error_bad_request") {
        // return error message from backend api
          history.output = `error: ${response.message}`;
          resolve(history);
        }
        //this is the error when a value that we search on doesn't exist
        else {
          history.output = `The desired value ${args[1]} doesn't exist in the loaded file.`;
          resolve(history);
        }
      });
    }
  };

  export {getAPIResponse, currentCSV, loadedHasHeader, clear}






