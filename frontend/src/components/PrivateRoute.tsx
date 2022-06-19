import { isLoggedIn } from "axios-jwt";
import { Navigate} from "react-router-dom";
import App from "../App";

const PrivateRoute = () => {
    return isLoggedIn() ? <App /> : <Navigate to="/login" />;
};

export default PrivateRoute;
