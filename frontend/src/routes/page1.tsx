import AuthenticationAPI from "../api/AuthenticationAPI";
import {useState} from "react";

export default function Page1() {
    const [status, setStatus] = useState("not called")

    return (
        <div>
            <h1>Page 1</h1>
            <p>Page one contents are soooo cool!</p>

            <button onClick={ () => {
                AuthenticationAPI.testAuthentication()
                    .then(test => {
                        console.log(`Test result = ${test.data}`)
                        setStatus(test.data.toString())
                    }, error => {
                        console.log(`Test error = ${error}`)
                        setStatus(error.toString())
                    })
            }}>Test Optional Auth</button>

            <p>Status: {status}</p>
        </div>
    )
}
