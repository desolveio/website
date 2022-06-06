import AuthenticationAPI from "../api/AuthenticationAPI";
import {useState} from "react";
import ProfileAPI from "../api/ProfileAPI";

export default function Page1() {
    const [status, setStatus] = useState("not called")
    const [information, setInformation] = useState("not called")

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

            <br></br>
            <button onClick={ () => {
                setInformation("loading...")

                ProfileAPI.grabInformation()
                    .then(test => {
                        console.log(`Test result = ${
                            JSON.stringify(test.data)
                        }`)
                        setInformation(JSON.stringify(test.data))
                    }, error => {
                        console.log(`Test error = ${error}`)
                        setInformation(error.toString())
                    })
            }}>Grab User Information</button>

            <p>Status: {status}</p>
            <p>Info: {information}</p>
        </div>
    )
}
