import { load, currentCSV, loadedHasHeader, search, view, clear} from "../src/api/API";
import { registerCommand } from "../src/components/InputBox";
import { mode, useMode, resetMode} from "../src/api/modeHandler";
import { Mode } from "../src/api/datatypes/ModeData";
import React, { useState } from "react";

beforeEach(() => {
  clear();
  resetMode();
});

//when we view with an extra argument, an error should be returned

describe("view function", () => {
  test("returns an error message when passed more than one argument", async () => {
    const args = ["view", "extra-arg"];
    const expected =
      "'view' doesn't take any arguments. Please type 'view' to view the loaded file.";
    const result = await view(args);
    expect(result.output).toBe(expected);
  });

  //view without loading a file should return an error

  test("returns an error message when no CSV file has been loaded", async () => {
    const args = ["view"];
    const expected = "error: no file was loaded.";
    const result = await view(args);
    expect(result.output).toBe(expected);
  });

});

//load should work with the proper arguments

test("returns a success message and the CSV data when successful", async () => {
  const args = ["load_file", "ten-star.csv"]
  const expected = "'ten-star.csv' successfully loaded.";
  const result = await load(args);
  expect(result.output).toBe(expected);
});

//when we load and then view, it should work

  test('returns a success message and the CSV data when successful', async () => {
    const args1 = ["load_file", "test.csv"]
    const args = ['view'];
    const expectedOutput = 'view successful. now displaying loaded file as a table.';
    const expectedData = [["StarID", "ProperName", "X", "Y", "Z"],
    ["0", "Sol", "0", "0", "0"],
    ["1", "Andreas", "282.43485", "0.00449", "5.36884"],
    ["2", "Rory", "43.04329", "0.00285", "-15.24144"],
    ["3", "Mortimer", "277.11358", "0.02422", "223.27753"]]
    const result1 = await load(args1)
    const result = await view(args);
    expect(result.output).toBe(expectedOutput);
    expect(result.data).toEqual(expectedData);
  });

  //an error should be returned when we try to load without providing a filepath

  describe("load function", () => {
    test("returns an error message when no filepath is provided", async () => {
      const args = ["load_file"];
      const expectedOutput =
        "load_file only accepts a filepath in the format 'load_file <filepath> <headersPresent> (optional)'.";
      const result = await load(args);
      expect(result.output).toBe(expectedOutput);
    });

    //when we pass in too many arguments for load_file, an error should be returned

    test("returns an error message when too many arguments are provided", async () => {
      const args = ["load_file", "data.csv", "true", "extra_argument"];
      const expectedOutput =
        "load_file only accepts a filepath in the format 'load_file <filepath> <headersPresent> (optional)'.";
      const result = await load(args);
      expect(result.output).toBe(expectedOutput);
    });

    //when we try to load a nonexistent CSV file, an error should be returned

    test("returns an error message when the filepath is invalid", async () => {
      const args = ["load_file", "nonexistent.csv"];
      const expectedOutput =
        "error: file could not be loaded. Please check the filepath and try again.";
      const result = await load(args);
      expect(result.output).toBe(expectedOutput);
    });

    //when we try to load on a CSV that exists loadedHasHeader should be false by default and the file should load

    test("returns a success message and sets the current CSV file when the CSV file is loaded", async () => {
      const args = ["load_file", "rand.csv"];
      const expectedOutput = "'rand.csv' successfully loaded.";
      const result = await load(args);
      expect(result.output).toBe(expectedOutput);
      expect(currentCSV).toBe("rand.csv");
      expect(loadedHasHeader).toBe(false);
    });

    //when we pass in true and load a file that exists, the file should be loaded and loadedHasHeader should be true 

    test("returns a success message and sets the current CSV file and header flag when the CSV file is loaded with headers", async () => {
      const args = ["load_file", "smallstardata.csv", "true"];
      const expectedOutput = "'smallstardata.csv' successfully loaded.";
      const result = await load(args);
      expect(result.output).toBe(expectedOutput);
      expect(currentCSV).toBe("smallstardata.csv");
      expect(loadedHasHeader).toBe(true);
    });
  });

  // test for successful search with a value to search for and a column number should return the row of the value that we searched on
test("search - successful search with query and columnID", async () => {
    const args1 = ["load_file", "test.csv"];
    const args = ["search", "Sol", "1"]
    const expectedOutput =
      "search successful";
    const expectedData = [["0", "Sol", "0", "0", "0"]];
    const result1 = await load(args1);
    const result = await search(args);
    expect(result.output).toBe(expectedOutput);
    expect(result.data).toEqual(expectedData);
});

//test for unsuccessful search when the value exists, but is not in the appropriate column number

test("search - successful search with query and columnID", async () => {
  const args1 = ["load_file", "test.csv"];
  const args = ["search", "Sol", "2"];
  const expectedOutput =
    "error: The inputted column number -2- does not contain desired value. Use a different column index or simply just pass in the desired value without a column identifier.";
  const result1 = await load(args1);
  const result = await search(args);
  expect(result.output).toBe(expectedOutput);
});

// when we test for a search that has a search value but not a column number
test("search - successful search with query and columnID", async () => {
  const args1 = ["load_file", "test.csv", "true"];
  const args = ["search", "ProperName"];
  const expectedOutput = "search successful";
  const expectedData = [["StarID", "ProperName", "X", "Y", "Z"]];
  const result1 = await load(args1);
  const result = await search(args);
  expect(result.output).toBe(expectedOutput);
  expect(result.data).toEqual(expectedData);
});

// when we search without loading a file, it should return a statement that says "no file was loaded"

test("search - error: no file loaded", async () => {
  const response = await search(["search", "item", "0"]);
  expect(response.output).toBe("error: no file was loaded.");
});

//when we search for too many arguments, there should be an error that says that we have passed in too many arguments

test("search - error: incorrect number of arguments", async () => {
  const args1 = ["load_file", "test.csv"];
  const result1 = await load(args1);
  const response = await search(["search", "item", "0", "extraArgument"]);
  expect(response.output).toBe(
    "search only accepts a query in the format 'search <query> <columnID> (optional)'."
  );
});

//test for searching a value that doesn't exist in the CSV, the appropriate error message should be printed

test("search - error: value not found", async () => {
  const args1 = ["load_file", "test.csv"];
  const result1 = await load(args1);
  const response = await search(["search", "nonexistent"]);
  expect(response.output).toBe(
    "error: desired value -nonexistent- was not found."
  );
});

 //when we test for a column number that is out of bonds, the appropriate error message should be returned

 test("search - error: column out of bonds", async () => {
   const args1 = ["load_file", "test.csv"];
   const result1 = await load(args1);
   const response = await search(["search", "nonexistent", "10"]);
   expect(response.output).toBe(
     "error: The inputted column number -10- is out of bounds. There are 5 columns. column index is zero-indexed."
   );
 });

 //when we change the mode to verbose, it should return the statement that says the mode is now verbose because we are starting in brief

 describe("mode function", () => {
   test("changing mode to verbose", async () => {
     const args = ["mode", "verbose"];
     const expectedOutput = "mode is now verbose.";
     const history = await mode(args);
     expect(history.output).toBe(expectedOutput);
   });

   //when we call mode brief, there should be a statement that tells us that the mode is now brief

   test("changing mode to brief", async () => {
     const args = ["mode", "brief"];
     const expectedOutput = "mode is now brief.";
     const history = await mode(args);
     expect(history.output).toBe(expectedOutput);
   });

   //when we call mode with mode with more than two arguments, there should be an error that is returned

   test("passing too many arguments", async () => {
     const args = ["mode", "extra", "arguments"];
     const expectedOutput =
       "must use 'mode brief' or 'mode verbose' to change the mode.";
     const history = await mode(args);
     expect(history.output).toBe(expectedOutput);
   });

   //when we call mode with the right number of arguments but the second argument isn't verbose or brief, an error should be returned
   
   test("passing an invalid argument", async () => {
     const args = ["mode", "invalid_argument"];
     const expectedOutput =
       "must use 'mode brief' or 'mode verbose' to change the mode.";
     const history = await mode(args);
     expect(history.output).toBe(expectedOutput);
   });
 });

