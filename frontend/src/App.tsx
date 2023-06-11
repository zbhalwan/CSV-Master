import {useEffect, useState} from "react";
import "../styles/App.css";
import Header from "./components/Header";
import InputBox from "./components/InputBox";
import HistoryBox from "./components/HistoryBox";
import {REPLFunction} from "./api/REPLFunction";
import {mode} from "./api/modeHandler";
import { Mode } from "./api/datatypes/ModeData";
import { CommandResponse } from "./api/datatypes/CommandResponse";

const mulitWordSplitter: RegExp =
  / (?=(?:[^\']*\'[^\']*\')*[^\']*$)|,(?=(?:[^\']*\"[^\']*\')*[^\']*$)/;


/*
  * This is the main App component that is rendered in the index.tsx file.
  * It contains the header, history box, and input box.
  * It also contains the state for the history and mode.
*/

  function App() {
  const [history, setHistory] = useState<CommandResponse[]>([]);
  const [mode, setMode] = useState(Mode.Brief);

  return (
    <div>
      <Header />
      <div className="repl">
        <div id="historyBox">
          <HistoryBox
            history={history}
            setHistory={setHistory}
            mode={mode}
            setMode={setMode}
          />
        </div>
        <hr />
        <div id="inputBox">
          <InputBox history={history} setHistory={setHistory} />
        </div>
      </div>
    </div>
  );
}


export default App;
