import React, {FormEventHandler, useState} from "react";
import AuthenticationAPI from "../../api/AuthenticationAPI";

export default function Register() {
    const [email, setEmail] = useState("")
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")

    const submit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()

        AuthenticationAPI.submitRegistration(email, username, password)
            .then(result => {
                console.log(`register result = ${JSON.stringify(result)}`)
            }, error => {
                console.log(`error result = ${error}`)
            })
    }

    return (
        <form onSubmit={submit}>
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
