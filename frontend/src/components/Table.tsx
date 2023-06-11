import { ReactElement } from "react";

/**
 * Table component
 */
interface TableBoxProps {
    data: string[][];
  }

    // this is dealing with any table creating that we are doing whether it's the search method or the view method 

export default function Table(props: TableBoxProps) {
    return (
      <table className="table table-danger table-striped-columns" role={"table"}>
        <tbody>
          {props.data.map((rows, i) => (
            <tr key={i}>
              {rows.map((cols, j) => (
                <td
                  key={j}
                  aria-label={i + 1 + " row " + (j + 1) + "column:" + cols}
                >
                  {cols}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    );
  }
