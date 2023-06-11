export const header_accessible_name = "REPL title";

// this is the header of the application

function Header() {
  return (
    <div
      aria-label={header_accessible_name}
      aria-describedby="Welcome to the REPL web app. Press the tab button to navigate to the input box to enter a command."
    >
      <h1>REPL!</h1>
    </div>
  );
}

export default Header;
