import "@testing-library/jest-dom";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import React from "react";
import { clear } from "../src/api/API";
import { CommandResponse } from "../src/api/datatypes/CommandResponse";
import { resetMode } from "../src/api/modeHandler";
import { REPLFunction } from "../src/api/REPLFunction";
import App from "../src/App";
import Header from "../src/components/Header";
import InputBox, { registerCommand, registry, removeCommand } from "../src/components/InputBox";
import { fireEvent } from '@testing-library/react';

beforeEach(() => {
    window.HTMLElement.prototype.scrollIntoView = function() {};
    render(<App />);
    resetMode();
    clear();
  });

describe("rendering app", () => {
	
    test("render header and button", () => {
		window.HTMLElement.prototype.scrollIntoView = function() {};
        // render(<App />);
		expect(screen.getByText(/REPL/i)).toBeInTheDocument();
		expect(screen.getByText(/Submit/i)).toBeInTheDocument();
	});
})

//when we load a file that doesn't exist, an error should be returned

describe("load_file", () => {
	test("loading wrong file and in brief mode by default", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");

		await userEvent.type(inputbox, "load_file wrong.csv");
		await userEvent.click(submit);

		expect(await screen.findByText("error: file could not be loaded. Please check the filepath and try again."))
		.toBeInTheDocument();
	});

    //when we load a file with no args[1] passed in, an error should be returned

    test("loading file with no args", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");

		await userEvent.type(inputbox, "load_file");
		await userEvent.click(submit);

		expect(await screen.findByText("load_file only accepts a filepath in the format 'load_file <filepath> <headersPresent> (optional)'."))
		.toBeInTheDocument();
	});

    //when we load a file with too many arguments, the error message below should be printed

    test("loading file w multiple args", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");

		await userEvent.type(inputbox, "load_file wrong.csv rand.csv");
		await userEvent.click(submit);

		expect(await screen.findByText("error: file could not be loaded. Please check the filepath and try again."))
		.toBeInTheDocument();
	});

    //when we load a correct file with tne proper number of arguments 

    test("loading correct file", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");

		await userEvent.type(inputbox, "load_file rand.csv");
		await userEvent.click(submit);

		expect(await screen.findByText("'rand.csv' successfully loaded."))
		.toBeInTheDocument();
	});

    //when we load a correct file with the header true the CSV should be loaded successfully

    test("loading correct file with header", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");

		await userEvent.type(inputbox, "load_file test.csv true");
		await userEvent.click(submit);

		expect(await screen.findByText("'test.csv' successfully loaded."))
		.toBeInTheDocument();
	});

})

//when we swich between mdoes verbose and brief, the appropriate statements should be printed

describe("testing mode", () => { 

    test("switching between mode", async () => {
		// render(<App />);

		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");

        //brief to verbose
		await userEvent.type(inputbox, "mode verbose");
		await userEvent.click(submit);
        expect(await screen.findByText("COMMAND: mode verbose")).toBeInTheDocument();
        expect(await screen.findByText("OUTPUT: mode is now verbose.")).toBeInTheDocument();

        // verbose to brief
        await userEvent.type(inputbox, "mode brief");
        await userEvent.click(submit);
        expect(await screen.findByText("mode is now brief.")).toBeInTheDocument();

        //brief to verbose
		await userEvent.type(inputbox, "mode verbose");
		await userEvent.click(submit);

        const subTitle = screen.getAllByText("COMMAND: mode verbose");
        expect(subTitle[0]).toBeInTheDocument();
        const subTitle2 = screen.getAllByText("OUTPUT: mode is now verbose.");
        expect(subTitle2[0]).toBeInTheDocument();
        


	});

    //when we input the wrong args[1] for mode, we should return an error saying that mode brief or mode verbose 

    test("mode incorrect arg", async () => {
		// render(<App />);
        
		let inputbox = screen.getByRole("inputbox");
		let submit = await screen.findByText("Submit");

		await userEvent.type(inputbox, "mode dog");
		await userEvent.click(submit);
        expect(await screen.findByText("must use 'mode brief' or 'mode verbose' to change the mode.")).toBeInTheDocument();
         
	});

          

})


describe("testing view", () => { 

    //this tests trying to view when a file isn't loaded, a statement regarding this should be printed
    
        test("viewing empty file", async () => {
            let inputbox = screen.getByRole("inputbox");
            let submit = await screen.findByText("Submit");
    
            await userEvent.type(inputbox, "view");
            await userEvent.click(submit);
            expect(await screen.findByText("error: no file was loaded.")).toBeInTheDocument();
        });

        //when we call view with too many arguments, it should say that view has taken too many arguments

        test("view with multiple args", async () => {
            let inputbox = screen.getByRole("inputbox");
            let submit = await screen.findByText("Submit");
    
            await userEvent.type(inputbox, "view file");
            await userEvent.click(submit);
            expect(await screen.findByText("'view' doesn't take any arguments. Please type 'view' to view the loaded file.")).toBeInTheDocument();
        });

        //when we try to call view with the right number of arguments along with loading a file, the view functionality should work properly 

        test("viewing loaded file", async () => {
            let inputbox = screen.getByRole("inputbox");
            let submit = await screen.findByText("Submit");
    
            await userEvent.type(inputbox, "load_file test.csv");
            await userEvent.click(submit);
            await userEvent.type(inputbox, "view");
            await userEvent.click(submit);

            let screen_table = await screen.findByRole("table");
            let table_cols = screen_table.children[0].children[0];
            let table_rows = screen_table.children[0];
            
            expect(table_cols.childElementCount).toBe(5);
            expect(table_rows.childElementCount).toBe(5);
            
            expect(await screen.findByText("Andreas")).toBeInTheDocument();
            expect(await screen.findByText("Mortimer")).toBeInTheDocument();
            expect(await screen.findByText("-15.24144")).toBeInTheDocument();
            expect(await screen.findByText("ProperName")).toBeInTheDocument();
        });
    
})


describe("testing search", () => { 

    //when we call search on a file that hasn't been loaded yet, an error should be returned saying that a file wasn't loaded 

    test("searching empty file", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "search Andreas");
        await userEvent.click(submit);
        expect(await screen.findByText("error: no file was loaded.")).toBeInTheDocument();
    });

    //when we call search without the right number of arguments, an error should be passed saying that we need more arguments

    test("searching with no args", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search");
        await userEvent.click(submit);
        expect(await screen.findByText("search only accepts a query in the format 'search <query> <columnID> (optional)'.")).toBeInTheDocument();
    });
    
    //when we search with too many arguments, an error should also be passed

    test("searching with multiple args", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search at no 12309 hello");
        await userEvent.click(submit);
        expect(await screen.findByText("search only accepts a query in the format 'search <query> <columnID> (optional)'.")).toBeInTheDocument();
    });

    //when we search on the incorrect column and pass in loadedHasHeader as false, it should let us know that the searchedValue doesn't exist on the column

    test("searching with incorrect column no headers", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv false");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas 3");
        await userEvent.click(submit);
        expect(await screen.findByText("error: The inputted column number -3- does not contain desired value. Use a different column index or simply just pass in the desired value without a column identifier.")).toBeInTheDocument();
    });

    //when we search with a header as false, but the csv itself doesn't have headers we should have an error letting us know that the CSV does not have any headers

    test("searching with header on file without headers declared", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv false");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas ProperName");
        await userEvent.click(submit);
        expect(await screen.findByText("error: CSV has no headers, use column index or simply just pass in the desired value without a column identifier.")).toBeInTheDocument();
    });

    //when we search with a column number out of bounds, we should get an error telling us that the column is out of bounds 

    test("searching with index out of bounds", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv false");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas 99");
        await userEvent.click(submit);
        expect(await screen.findByText("error: The inputted column number -99- is out of bounds. There are 5 columns. column index is zero-indexed.")).toBeInTheDocument();
    });

    //when we search without a column number,the right number of arguments, and a valid value in the search, the row on the value that is searched should be returned

    test("searching w/o columnID", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv false");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas");
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

    //when we search with the appropriate column number, search value, and the right number of arguments the row of the value that we search on should be returned


    test("searching w correct column index", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv false");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas 1");
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

    //when we search and the column has a header and we pass in the right boolean value, the row that we search on should be passed

    test("searching w correct header", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv true");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas ProperName");
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

    //when we search with a header as true, and we try to search on that header, an error should be returned beacuse that row shouldn't exist anymore


    test("searching w incorrect header", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "load_file test.csv true");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas StarID");
        await userEvent.click(submit);

        expect(await screen.findByText("error: The column StarID does not contain the desired value. Here is a list of present column headers: [StarID, ProperName, X, Y, Z]")).toBeInTheDocument();
        
    });

    //this test utilizies a combination of all the functions and makes sure that they interact together appropriately

    test("testing all 4 commands", async () => {
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");

        await userEvent.type(inputbox, "mode verbose");
        await userEvent.click(submit);

        const subTitle = screen.getAllByText("COMMAND: mode verbose");
        expect(subTitle[0]).toBeInTheDocument();
        const subTitle2 = screen.getAllByText("OUTPUT: mode is now verbose.");
        expect(subTitle2[0]).toBeInTheDocument();


        await userEvent.type(inputbox, "load_file test.csv true");
        await userEvent.click(submit);
        await userEvent.type(inputbox, "search Andreas ProperName");
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



        await userEvent.type(inputbox, "view");
        await userEvent.click(submit);

        
        expect(await screen.findByText("StarID")).toBeInTheDocument();
        expect(await screen.findByText("Sol")).toBeInTheDocument();
        expect(await screen.findByText("-15.24144")).toBeInTheDocument();
        expect(await screen.findByText("223.27753")).toBeInTheDocument();


    });
    

})

    //this test utilizies a combination of all the functions and makes sure that they interact together appropriately

describe('Testing InputBox with and with out Shortcuts', async () => {
    test('should call handleKeyDown and populate the input box with the corresponding command', async () => {
    //   const { container } = render(<InputBox />);
        let inputbox = screen.getByRole("inputbox");
        let submit = await screen.findByText("Submit");  
        
        fireEvent.keyDown(window, { ctrlKey: true, key: ']' });
        expect(inputbox).toHaveValue('load_file');

        fireEvent.keyDown(window, { ctrlKey: true, key: '.' });
        expect(inputbox).toHaveValue('view');

        fireEvent.keyDown(window, { ctrlKey: true, key: 's' });
        expect(inputbox).toHaveValue('search');

        fireEvent.keyDown(window, { ctrlKey: true, key: 'b' });
        expect(inputbox).toHaveValue('mode brief');

        fireEvent.keyDown(window, { ctrlKey: true, key: 'v' });
        expect(inputbox).toHaveValue('mode verbose');

        await userEvent.type(inputbox, "clear");
		await userEvent.click(submit);

        await userEvent.type(inputbox, "zeeshan");
        expect(inputbox).toHaveValue('zeeshan');

        await userEvent.type(inputbox, "clear");
		await userEvent.click(submit);

        await userEvent.type(inputbox, "search");
        expect(inputbox).toHaveValue('search');

        
    });
  });










