import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import React from "react";
import { APIResponse } from "../src/api/datatypes/APIResponse";
import { CommandResponse } from "../src/api/datatypes/CommandResponse";
import { resetMode } from "../src/api/modeHandler";
import { REPLFunction } from "../src/api/REPLFunction";
import App from "../src/App";
import Header from "../src/components/Header";
import { registerCommand, registry, removeCommand } from "../src/components/InputBox";


/**
 * before each test, we render the App component
 * and reset the mode to brief
 */
beforeEach(() => {
    window.HTMLElement.prototype.scrollIntoView = function() {};
    render(<App />);
    resetMode();
  });

// MOCKING
describe("tests with mock", () => {
  
  /**
   * mock function for mode command
   * @param args the mode command takes 
   * @returns 
   */
  const modeMockReplFunction: REPLFunction = async (args: string[]) => {
    return new Promise<CommandResponse>((resolve) => {
      resolve({
        command: "mock_mode verbose",
        output: mockAPIResponseMode.result,
        data: mockAPIResponseMode.data,
        hasHeader: false,
      });
    });
  }

  const mockAPIResponseMode = {
    message: "success mode change",
    result: "Command: mode verbose Output: mode changed to verbose",
    data: undefined  
}



  
  
  /**
   * mock function for load command
   * @param args the load command takes 
   * @returns 
   */
  const loadMockReplFunction: REPLFunction = async (args: string[]) => {
        return new Promise<CommandResponse>((resolve) => {
          resolve({
            command: "mock_load hello_world",
            output: mockAPIResponseLoad.result,
            data: mockAPIResponseLoad.data,
            hasHeader: false,
          });
        });
      }
  /**
   * mock API response for load command
   */
      const mockAPIResponseLoad = {
        message: "success load",
        result: "hello_world loaded successfully",
        data: undefined  
    }
    /**
     * mock function for view command
     */
    const mockAPIResponseView = {
        message: "success view",
        result: "successful test",
        data: [
          ["StarID", "ProperName", "X", "Y", "Z"],
          ["0", "Sol", "0", "0", "0"],
          ["1", "Andreas", "282.43485", "0.00449", "5.36884"],
          ["2", "Rory", "43.04329", "0.00285", "-15.24144"],
          ["3", "Mortimer", "277.11358", "0.02422", "223.27753"],
          ["4", "Bailee", "79.62896", "0.01164", "-101.53103"],
          ["5", "Zita", "264.58918", "0.04601", "-226.71007"],
        ]  
    }
/**
 * mock api response for search
 */
    const mockAPIResponseSearch = {
      message: "success search",
      result: "successful test",
      data: [
        ["1", "Andreas", "282.43485", "0.00449", "5.36884"], 
      ]  
  }
  /**
   * mock api response for search error
   */
    const mockAPIResponseSearchErorr = {
      message: "error while searching",
      result: "desired value -zeeshan- not found",
      data: undefined  
  }

  /**
   * mock function for view command
   * @param args the view command takes 
   * @returns 
   */
    const viewMockReplFunction: REPLFunction = async (args: string[]) => {
      return new Promise<CommandResponse>((resolve) => {
        resolve({
          command: "view_mock",
          output: mockAPIResponseView.result,
          data: mockAPIResponseView.data,
          hasHeader: false,
        });
      });
    }

/**
   * mock function for search command
   * @param args the search command takes 
   * @returns 
   */
    const searchMockReplFunction: REPLFunction = async (args: string[]) => {
      return new Promise<CommandResponse>((resolve) => {
        resolve({
          command: "search_mock Andreas",
          output: mockAPIResponseSearch.result,
          data: mockAPIResponseSearch.data,
          hasHeader: true,
        });
      });
    }

/**
   * mock function for search command
   * @param args the search command takes 
   * @returns 
   */
    const searchErrorMockReplFunction: REPLFunction = async (args: string[]) => {
      return new Promise<CommandResponse>((resolve) => {
        resolve({
          command: "mock_search zeeshan",
          output: mockAPIResponseSearchErorr.result,
          data: mockAPIResponseSearchErorr.data,
          hasHeader: true,
        });
      });
    }

/**
 * test mock mode switching to verbose mode
 */
    test("mode switch to verbose", async () => {
      // render(<App />);
    
          let inputbox = screen.getByRole("inputbox");
          let submit = await screen.findByText("Submit");
          registerCommand("mock_mode", modeMockReplFunction);
    
          await userEvent.type(inputbox, "mock_mode vebose");
          await userEvent.click(submit);
    
          expect(await screen.findByText("Command: mode verbose Output: mode changed to verbose")).toBeInTheDocument();
    });

/**
 * testing succesful laod command
 */
      test("testing load_file brief mode output on screen with mock", async () => {
		// render(<App />);

        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");
        registerCommand("mock_load", loadMockReplFunction);

        await userEvent.type(inputbox, "mock_load hello_world");
        await userEvent.click(submit);

        expect(await screen.findByText("hello_world loaded successfully")).toBeInTheDocument();
	});

/**
 * testing succesful view command
 */
    test("testing view brief mode table output on screen with mock", async () => {
		// render(<App />);

		  let inputbox = screen.getByRole("inputbox");
		  let submit = await screen.findByText("Submit");
      registerCommand("view_mock", viewMockReplFunction);

		  await userEvent.type(inputbox, "view_mock");
		  await userEvent.click(submit);

      let screen_table = await screen.findByRole("table");
		  let table_cols = screen_table.children[0].children[0];
      let table_rows = screen_table.children[0];
		  expect(table_cols.childElementCount).toBe(5);
      expect(table_rows.childElementCount).toBe(7);

      expect(await screen.findByText("StarID")).toBeInTheDocument();
      expect(await screen.findByText("Bailee")).toBeInTheDocument();
      expect(await screen.findByText("0.04601")).toBeInTheDocument();
      expect(await screen.findByText("223.27753")).toBeInTheDocument();
      expect(await screen.findByText("successful test")).toBeInTheDocument();
	});

/**
 * testing view with verbose mode
 */
    test("testing verbose mode table output on screen with mock", async () => {
		// render(<App />);

      let inputbox = screen.getByRole("inputbox");
      let submit = await screen.findByText("Submit");
          
      registerCommand("view_mock", viewMockReplFunction);

      await userEvent.type(inputbox, "mode verbose");
      await userEvent.click(submit);
      await userEvent.type(inputbox, "view_mock");
      await userEvent.click(submit);

      let screen_table = await screen.findByRole("table");
      let table_cols = screen_table.children[0].children[0];
      let table_rows = screen_table.children[0];
      expect(table_cols.childElementCount).toBe(5);
      expect(table_rows.childElementCount).toBe(7);

      expect(await screen.findByText("StarID")).toBeInTheDocument();
      expect(await screen.findByText("Sol")).toBeInTheDocument();
      expect(await screen.findByText("0.04601")).toBeInTheDocument();
      expect(await screen.findByText("223.27753")).toBeInTheDocument();
      expect(await screen.findByText("COMMAND: mode verbose")).toBeInTheDocument();
      expect(await screen.findByText("OUTPUT: mode is now verbose.")).toBeInTheDocument();
      expect(await screen.findByText("COMMAND: view_mock")).toBeInTheDocument();
      expect(await screen.findByText("OUTPUT: successful test")).toBeInTheDocument();
	});


/**
 * testing successful search command
 */
    test("testing search brief mode table output on screen with mock", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");
        registerCommand("search_mock", searchMockReplFunction);

		await userEvent.type(inputbox, "search_mock Andreas");
		await userEvent.click(submit);

        let screen_table = await screen.findByRole("table");
		let table_cols = screen_table.children[0].children[0];
        let table_rows = screen_table.children[0];
		expect(table_cols.childElementCount).toBe(5);
        expect(table_rows.childElementCount).toBe(1);

        expect(await screen.findByText("1")).toBeInTheDocument();
        expect(await screen.findByText("Andreas")).toBeInTheDocument();
        expect(await screen.findByText("282.43485")).toBeInTheDocument();
        expect(await screen.findByText("0.00449")).toBeInTheDocument();
        expect(await screen.findByText("5.36884")).toBeInTheDocument();
	});

/**
 * testing search with verbose mode
 */
  test("testing search verbose mode table output on screen with mock", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");
    registerCommand("search_mock", searchMockReplFunction);

		await userEvent.type(inputbox, "mock_mode verbose");
		await userEvent.click(submit);
    await userEvent.type(inputbox, "search_mock Andreas");
		await userEvent.click(submit);

    let screen_table = await screen.findByRole("table");
		let table_cols = screen_table.children[0].children[0];
    let table_rows = screen_table.children[0];
		expect(table_cols.childElementCount).toBe(5);
    expect(table_rows.childElementCount).toBe(1);

    expect(await screen.findByText("Command: mode verbose Output: mode changed to verbose")).toBeInTheDocument();
    expect(await screen.findByText("1")).toBeInTheDocument();
    expect(await screen.findByText("Andreas")).toBeInTheDocument();
    expect(await screen.findByText("282.43485")).toBeInTheDocument();
    expect(await screen.findByText("0.00449")).toBeInTheDocument();
    expect(await screen.findByText("5.36884")).toBeInTheDocument();
	});

/**
 * testing search error
 */
  test("testing search error", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");
        registerCommand("mock_search", searchErrorMockReplFunction);

		await userEvent.type(inputbox, "mock_search zeeshan");
		await userEvent.click(submit);

    expect(await screen.findByText("desired value -zeeshan- not found")).toBeInTheDocument();
	});


/**
 * testing all 4 commands in conjunction
 */

  test("testing verbose mode with load_file and search and view", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");
    registerCommand("search_mock", searchMockReplFunction);

    await userEvent.type(inputbox, "mock_load hello_world");
    await userEvent.click(submit);
    expect(await screen.findByText("hello_world loaded successfully")).toBeInTheDocument();
		
    
    await userEvent.type(inputbox, "mock_mode verbose");
		await userEvent.click(submit);
    await userEvent.type(inputbox, "search_mock Andreas");
		await userEvent.click(submit);

    let screen_table = await screen.findByRole("table");
		let table_cols = screen_table.children[0].children[0];
    let table_rows = screen_table.children[0];
		expect(table_cols.childElementCount).toBe(5);
    expect(table_rows.childElementCount).toBe(1);

    expect(await screen.findByText("Command: mode verbose Output: mode changed to verbose")).toBeInTheDocument();
    expect(await screen.findByText("1")).toBeInTheDocument();
    expect(await screen.findByText("Andreas")).toBeInTheDocument();
    expect(await screen.findByText("282.43485")).toBeInTheDocument();
    expect(await screen.findByText("0.00449")).toBeInTheDocument();
    expect(await screen.findByText("5.36884")).toBeInTheDocument();



    await userEvent.type(inputbox, "view_mock");
    await userEvent.click(submit);

    expect(await screen.findByText("StarID")).toBeInTheDocument();
    expect(await screen.findByText("Bailee")).toBeInTheDocument();
    expect(await screen.findByText("0.04601")).toBeInTheDocument();
    expect(await screen.findByText("223.27753")).toBeInTheDocument();
	});

})