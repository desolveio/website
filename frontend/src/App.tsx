import './routes/App.css';
import {Link, Outlet} from "react-router-dom";

function App() {
    return (
        <div className={"primary"}>
            <h1>Desolve</h1>

            <nav>
                <Link to="/">Index</Link> |{" "}
                <Link to="/home">Home</Link> |{" "}
                <Link to="/page1">Page 1</Link> |{" "}
                <Link to="/page2">Page 2</Link> |{" "}
                <Link to="/login">Login</Link> |{" "}
                <Link to="/register">Register</Link>
            </nav>

            <Outlet/>
        </div>
    );
}

export default App;
