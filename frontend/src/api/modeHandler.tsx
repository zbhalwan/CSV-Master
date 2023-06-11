import { useState, useEffect } from "react";
import { CommandResponse } from "./datatypes/CommandResponse";
import { Mode } from "./datatypes/ModeData";
import { REPLFunction } from "./REPLFunction";


export function resetMode() {
  globalMode = Mode.Brief;
}

let globalMode = Mode.Brief;

/*
*this entire class is handling everything to do with switching between modes verbose and brief
*/ 

export function useMode(): Mode {
  const [mode, setMode] = useState<Mode>(Mode.Brief);
  useEffect(() => {setMode(globalMode);  }, 
  [globalMode]);
  return mode;
}

export const mode: REPLFunction = (args: string[]) => {
  let history: CommandResponse = {
    command: args.join(" "),
    output: "",
    data: undefined,
    hasHeader: false,
  };

  //if the user passes in too many arguments (greater than two), an error should be returned

  if (args.length !== 2) {
    return new Promise<CommandResponse>((resolve) => {
      history.output =
        "must use 'mode brief' or 'mode verbose' to change the mode.";
      resolve(history);
    });

    //when the second argument is verbose we are switching the mode to verbose and returning the appropriate message

  } else if (args[1] === "verbose") {
    globalMode = Mode.Verbose;
    return new Promise<CommandResponse>((resolve) => {
      history.output = "mode is now verbose.";
      resolve(history);
    });

    //here, we are switching the mode to brief

  } else if (args[1] === "brief") {
    globalMode = Mode.Brief;
    return new Promise<CommandResponse>((resolve) => {
      history.output = "mode is now brief.";
      resolve(history);
    });

    //if anything else is passed in, we are returning an error once again
    
  } else {
    return new Promise<CommandResponse>((resolve) => {
      history.output =
        "must use 'mode brief' or 'mode verbose' to change the mode.";
      resolve(history);
    });
  }
};