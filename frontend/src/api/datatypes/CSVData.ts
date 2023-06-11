// Description: Data type for CSV data
export type ParsedCSV = {
    path: string;
    headers: string[] | null;
    dataArray: string[][];
  };