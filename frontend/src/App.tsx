import './routes/App.css';
import {Link, Outlet, useNavigate} from "react-router-dom";
import {clearAuthTokens, isLoggedIn} from "axios-jwt";
import AuthenticationAPI from "./api/AuthenticationAPI";

function App() {
    const navigate = useNavigate();

    const handleLogout = () => {
        const logout = () => {
            console.log("Successfully logged out!")
            clearAuthTokens()

            navigate('/');
        }

        AuthenticationAPI.submitLogout()
            .then(logout, logout)
    }

    return (
        <div className={"primary"}>
            <h1>Desolve</h1>

            <nav>
                <Link to="/">Index</Link> |{" "}
                <Link to="/page1">Page 1</Link> |{" "}
                <Link to="/page2">Page 2</Link> |{" "}

                {isLoggedIn() ? (
                        <Link to="/" onClick={ handleLogout }>Logout</Link>
                ) : (
                   <>
                        <Link to="/login">Login</Link> |{" "}
                        <Link to="/register">Register</Link>
                   </>
                )}




            </nav>

            <Outlet/>
        </div>
    );
}

export default App;
