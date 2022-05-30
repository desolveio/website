import React, {FormEventHandler, useState} from "react";
import RegisterAPI from "../../api/RegisterAPI";

export default function Register() {
    const [email, setEmail] = useState("")
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")

    const submit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()

        RegisterAPI.submitRegistration(email, username, password)
            .then(result => {
                console.log(`login result = ${JSON.stringify(result)}`)
            }, error => {
                console.log(`error result = ${error}`)
            })
    }

    return (
        <form onSubmit={submit}>
            <label>Email:</label>
            <input type="text" value={email} onChange={e => setEmail(e.target.value)}/>

            <label>Username:</label>
            <input type="text" value={username} onChange={e => setUsername(e.target.value)}/>

            <label>Password:</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)}/>

            <input type="submit" value="Register"/>
        </form>
    )
}
