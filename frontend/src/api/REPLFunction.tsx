import { CommandResponse } from "./datatypes/CommandResponse";
/**
 * A function that takes in an array of strings and returns a promise of a CommandResponse
 */
export interface REPLFunction {    
    (args: Array<string>): Promise<CommandResponse>
}