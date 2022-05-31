import React, {FormEventHandler, useState} from "react";
import AuthenticationAPI from "../../api/AuthenticationAPI";
import {setAuthTokens} from "axios-jwt";
import {useNavigate} from "react-router-dom";

export default function Login() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const navigate = useNavigate();

    const submit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()

        AuthenticationAPI.submitLogin(email, password)
            .then(result => {
                console.log(`login result = ${JSON.stringify(result.data)}`)
                console.log(`refresh result = ${JSON.stringify(result.data.refreshToken)}`)

                if (result.data.accessToken) {
                    setAuthTokens({
                        accessToken: result.data.accessToken,
                        refreshToken: result.data.refreshToken.token,
                    })

                    navigate('/page1');
                } else {
                    // TODO: Handle error
                    console.log(`login error = ${JSON.stringify(result.data)}`)
                }
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
