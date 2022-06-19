import { isLoggedIn } from "axios-jwt";
import { Navigate, Outlet } from "react-router-dom";

const AnonRoute = () => {
    return !isLoggedIn() ? <Outlet /> : <Navigate to="/" />;
};

export default AnonRoute;
