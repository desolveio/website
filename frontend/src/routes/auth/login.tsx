import React, {FormEventHandler, useState} from "react";
import LoginAPI from "../../api/LoginAPI";

export default function Login() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")

    const submit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()

        LoginAPI.submitLogin(email, password)
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

            <label>Password:</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)}/>

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
