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
import Page404 from "./routes/404";
import BuildArtifacts from "./routes/upload";

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

root.render(
    <BrowserRouter>
        <SetupAxios/>
        <Routes>
            <Route path="/" element={<App/>}>
                <Route path="404" element={<Page404/>}/>

                <Route path="page1" element={<Page1/>}/>
                <Route path="page2" element={<Page2/>}/>

                <Route path="login" element={<Login/>}/>
                <Route path="register" element={<Register/>}/>

                <Route path="build" element={<BuildArtifacts/>}/>
                <Route path="users/:username" element={<UserView/>}/>
            </Route>
        </Routes>

    </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
