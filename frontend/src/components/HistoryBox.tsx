import { CommandResponse } from "../api/datatypes/CommandResponse";
import { Mode } from "../api/datatypes/ModeData";
import { useEffect, useRef } from "react";
import HistoryItem from "./HistoryContainer";
import { useMode } from "../api/modeHandler";

/*
 * This component is responsible for displaying the history of commands and outputs
 */

interface HistoryBoxProps {
  history: CommandResponse[];
  setHistory: (history: CommandResponse[]) => void;
  mode: Mode;
  setMode: (mode: Mode) => void;
}

function HistoryBox(props: HistoryBoxProps) {
  const historyBoxRef = useRef<null | HTMLDivElement>(null);

  const scrollToBottom = () => {
    if (historyBoxRef.current !== null) {
      historyBoxRef.current.scrollIntoView({ behavior: "smooth" });
    }
  };


  
  useEffect(() => {
    scrollToBottom();
  }, [props.history]);

  return (
    <div
      className="repl-history"
      // the element should be focusable in sequential keyboard navigation
      tabIndex={0}
      // the element should be focusable with a pointing device
      role="button"
      aria-label={"history data of commands and outputs"}
      aria-describedby="This is the history of the REPL application. It contains the commands that have been entered and the output of those commands."
    >
      {props.history.map((command, index) => {
        return (
          <HistoryItem
            key={index}
            mode={useMode}
            command={command.command}
            output={command.output}
            data={command.data}
            hasHeader={command.hasHeader}
          />
        );
      })}
      <div className="scroll-to-bottom" ref={historyBoxRef} />
    </div>
  );
}

export default HistoryBox;