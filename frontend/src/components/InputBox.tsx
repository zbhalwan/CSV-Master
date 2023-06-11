import { useState } from "react";
import { CommandResponse } from "../api/datatypes/CommandResponse";
import { mode } from "../api/modeHandler";
import { load,  view, search, currentCSV} from "../api/API";
import { REPLFunction } from "../api/REPLFunction";

/* 
  This file contains the InputBox component. This component is responsible for handling the user input and displaying the appropriate messages.
*/

export const input_box_accessible_name = "command input box";
export const button_accessible_name =
  "submit button to enter commands";

  

interface InputBoxProps {
  history: CommandResponse[];
  setHistory: (data: CommandResponse[]) => void;
}

const registry: Map<string, REPLFunction> = new Map<string, REPLFunction>();

//this function is registering all of the defaul user sotry commands that we have created by connecting our frontend to backend

function registerDefaultCommands() {
  registerCommand("mode", mode);
  registerCommand("load_file", load);
  registerCommand("view", view);
  registerCommand("search", search);
}

//this is the helper function that is used above 

/**
 * adds new command to registry for use in REPL
 * @param name of command
 * @param commandFunction REPLFunction that is called when command is entered 
 */
function registerCommand(commandName: string, commandFunction: REPLFunction): void {
  registry.set(commandName, commandFunction);
}




/**
 * removes command from registry
 * @param name of command
 * @param commandFunction REPLFunction that is called when command is entered 
 */
export function removeCommand(commandName: string): void {
  registry.delete(commandName);
}

//this is the message that is displayed that guides the user to type in the appropriate message in the InputBox

export default function InputBox(props: InputBoxProps) {
  registerDefaultCommands();

  const [command, setCommand] = useState<string>("");
  


  function handleKeyDown(event: KeyboardEvent) {
    if (event.ctrlKey) {
        if (event.key === ']') {
            setCommand("load_file");
        } else if (event.key === '.') {
            setCommand("view");
        } else if (event.key === 's') {
            setCommand("search");
        } else if (event.key === 'v') {
            setCommand("mode verbose");
        }
         else if (event.key === 'b') {
          setCommand("mode brief");
      }
    }
}
window.addEventListener('keydown', handleKeyDown);




  //if the command passed in is "clear" we are emptying out the history by setting it to an empty array... in any other case we are adding the output to the existing history
  async function handleSubmit() {
    if (command === "clear") {
      props.setHistory([]);
      setCommand("");
    } else {
      let output: CommandResponse = await handleCommand();
      props.setHistory([...props.history, output]);
      setCommand("");
  }
}

  /**
   * helper function that is used to handle all of the messages that are printed out according to the input of the user
   */

  async function handleCommand(): Promise<CommandResponse> {
    let inputs: string[] = command.split(" ");
    let functionToCall: REPLFunction | undefined = registry.get(inputs[0]);

    return new Promise((resolve) => {
      if (functionToCall !== undefined) {
        resolve(functionToCall(inputs));
      }
      else {
        resolve({
          command: command,
          output: `command '${inputs[0]}' not found. available commands: load_file, mode, view, search.`,
          data: undefined,
          hasHeader: false,
        });
      }
    });
  }

  return (
    <div className="repl-input">
      {/* TODO: Make this input box sync with the state variable */}
      <input
        type="text"
        className="repl-command-box"
        role="inputbox"
        aria-label={input_box_accessible_name}
        aria-description="please input a command here and then hit the enter key or press the submit button"
        value={command}
        tabIndex={0}
        onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
          setCommand(e.target.value)
        }
        onKeyUp={(e: React.KeyboardEvent<HTMLInputElement>) => {
          if (e.key === "Enter") {
            handleSubmit();
          }
        }}
      />
      <button
        className="repl-button"
        role={"button"}
        onClick={handleSubmit}
        aria-label={button_accessible_name}
        aria-description="use this button or the enter key to input a
              command into the program"
      >
        Submit
      </button>
    </div>
  );
}

export {registerCommand, registry}