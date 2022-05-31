import './routes/App.css';
import {Link, Outlet} from "react-router-dom";
import {clearAuthTokens, isLoggedIn} from "axios-jwt";

function App() {
    return (
        <div className={"primary"}>
            <h1>Desolve</h1>

            <nav>
                <Link to="/">Index</Link> |{" "}
                <Link to="/page1">Page 1</Link> |{" "}
                <Link to="/page2">Page 2</Link> |{" "}

                {isLoggedIn() ? (
                        <Link to="/" onClick={ () => {
                            clearAuthTokens()
                            console.log("logged out")
                        } }>Logout</Link>
                ) : (
                    <div>
                        <Link to="/login">Login</Link> |{" "}
                        <Link to="/register">Register</Link> |{" "}
                    </div>
                )}




            </nav>

            <Outlet/>
        </div>
    );
}

export default App;
