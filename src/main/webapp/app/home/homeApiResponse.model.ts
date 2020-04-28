export class ApiResponse {
  constructor(public message?: string, public error?: boolean) {
    this.error = false;
  }
}
