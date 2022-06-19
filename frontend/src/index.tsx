import React from 'react';
import ReactDOM from 'react-dom/client';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import Page1 from "./routes/page1";
import Page2 from "./routes/page2";
import Login from "./routes/auth/login";
import Register from "./routes/auth/register";
import {SetupAxios} from "./utils/AxiosUtil";
import UserView from "./routes/profile/userView";
import BuildArtifacts from "./routes/build/build";
import SetupView from "./routes/setup/setup";
import PrivateRoute from "./components/PrivateRoute";
import AnonRoute from "./components/AnonRoute";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

root.render(
    <BrowserRouter>
        <SetupAxios/>
        <Routes>
            <Route element={<PrivateRoute />}>
                <Route path="/build" element={<BuildArtifacts/>} />
            </Route>

            <Route element={<AnonRoute />}>
                <Route path="/login" element={<Login/>}/>
                <Route path="/register" element={<Register/>}/>
            </Route>

            <Route path="/" element={<App/>}>
                <Route path="page1" element={<Page1/>}/>
                <Route path="page2" element={<Page2/>}/>

                <Route path="users/:username" element={<UserView/>}/>
                <Route path="setup/:buildTool" element={<SetupView/>}/>
            </Route>
        </Routes>

    </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
