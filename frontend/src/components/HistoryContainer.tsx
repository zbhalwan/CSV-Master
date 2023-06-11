import { Mode } from "../api/datatypes/ModeData";
import Table from "./Table";


export const hist_output_accessible_name = "command result in history";
export const hist_command_accessible_name = "a command stored in the history";
export const data_table_accessible_name = "data table";

/*
* This is the history item component that is used to display the individual history of the commands and the outputs
*/
export interface HistoryItemProps {
  mode: () => Mode;
  command: string;
  output: string;
  data: string[][] | undefined;
  hasHeader: boolean;
}

/*
this function is dealing with the messages that we are dealing with in the history
*/

function HistoryItem(props: HistoryItemProps) {
  //when the mode is brief we are displaying the appropriate message
  if (props.mode() === Mode.Brief) {
    return (
      <div className="history-item">
        <div
          className="output"
          aria-label={hist_output_accessible_name}
          aria-description="This is a result stored in the history log."
        >
          {props.output}
          {props.data !== undefined ? <Table data={props.data} /> : null}
        </div>
        <hr />
      </div>
    );

    // when the mode is verbose, we are displaying the appropriate messages
  } else {
    return (
      <div className="history-item">
        <div
          className="command"
          aria-description="This is a command stored in the history log."
          aria-describedby={props.command}
        >
          COMMAND: {props.command}
        </div>
        <div
          className="output"
          aria-label={hist_output_accessible_name}
          aria-description="This is a result stored in the history log."
        >
          OUTPUT: {props.output}
        </div>
        <div
          className="data-table"
          aria-label={data_table_accessible_name}
          aria-description="This is a data table in the history log"
        >
          {props.data !== undefined ? <Table data={props.data} /> : null}
          <hr />
        </div>
      </div>
    );
  }
}

export default HistoryItem;