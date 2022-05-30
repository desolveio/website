import React, {FormEventHandler, useState} from "react";
import AuthenticationAPI from "../../api/AuthenticationAPI";

export default function Login() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")

    const submit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()

        AuthenticationAPI.submitLogin(email, password)
            .then(result => {
                console.log(`login result = ${JSON.stringify(result)}`)
            }, error => {
                console.log(`error result = ${error}`)
            })
    }

    return (
        <form onSubmit={submit}>
            <br></br>
            <label>Email: </label>
            <input type="text" value={email} onChange={e => setEmail(e.target.value)}/><br></br>

            <label>Password: </label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)}/><br></br>

            <input type="submit" value="Login"/>
        </form>
    )
}

/*
export default class Login extends React.Component
    {

        handleSubmit()
        {

        }

        render()
        {
            return (
                <div>
                    <form onSubmit={this.handleSubmit}>

                    </form>
                </div>
            )
        }
    }
*/
