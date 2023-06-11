/**
 * APIResponse is the response type for all API calls and stores the response
 */
export interface APIResponse {
    message: string
    result: string,
    data: string[][] | undefined,
  }