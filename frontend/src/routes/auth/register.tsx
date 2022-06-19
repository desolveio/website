import React, {useState} from "react";
import AuthenticationAPI from "../../api/AuthenticationAPI";
import {isLoggedIn} from "axios-jwt";
import {Navigate} from "react-router-dom";

let registrationEnabled = false

export default function Register() {
    const [email, setEmail] = useState("")
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")

    const [status, setStatus] = useState("")

    const submit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()

        if (!registrationEnabled) {
            setStatus("Registration is disabled on the beta site.")
            return
        }

        AuthenticationAPI.submitRegistration(email, username, password)
            .then(result => {
                setStatus(result.data.toString())
            }, error => {
                console.log(`error result = ${error}`)
                setStatus("registration failed")
            })
    }

    return (
        <form onSubmit={submit}>
            <br></br>
            <p>{status}</p>
            <br></br>
            <label>Email: </label>
            <input type="text" value={email} onChange={e => setEmail(e.target.value)}/><br></br>

            <label>Username: </label>
            <input type="text" value={username} onChange={e => setUsername(e.target.value)}/><br></br>

            <label>Password: </label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)}/><br></br>

            <input type="submit" value="Register"/>
        </form>
    )
}
