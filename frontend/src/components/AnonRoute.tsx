import { isLoggedIn } from "axios-jwt";
import { Navigate} from "react-router-dom";
import App from "../App";

const AnonRoute = () => {
    return !isLoggedIn() ? <App /> : <Navigate to="/" />;
};

export default AnonRoute;
