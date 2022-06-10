import './routes/App.css';
import {Link, Outlet, useNavigate} from "react-router-dom";
import {clearAuthTokens, isLoggedIn} from "axios-jwt";
import AuthenticationAPI from "./api/AuthenticationAPI";
import {useState} from "react";

function App() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("")

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
                    <>
                        <Link to="/build">Build</Link> |{" "}
                        <Link to="/" onClick={ handleLogout }>Logout</Link>
                    </>
                ) : (
                   <>
                        <Link to="/login">Login</Link> |{" "}
                        <Link to="/register">Register</Link>
                   </>
                )}

                <br></br><br></br>

                <input type="text" placeholder="Type a username..." onChange={
                    event => setUsername(event.target.value)
                }></input>{" "}

                <button onClick={ () => {
                    navigate('users/' + username)
                }}>Search</button>
            </nav>
            <Outlet/>
        </div>
    );
}

export default App;
