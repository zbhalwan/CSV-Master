/**
 * The data obtained from a command.
 */
export interface CommandResponse {
    command: string;
    output: string;
    data: string[][] | undefined ;
    hasHeader: boolean;
  }