import React, {useState} from "react";
import AuthenticationAPI from "../../api/AuthenticationAPI";
import {Link, useParams} from "react-router-dom";

export default function RegisterVerify() {
    const params = useParams()
    const [data, setData] = useState({
        updated: false, success: false, description: ""
    })

    // @ts-ignore
    AuthenticationAPI.submitRegistrationVerification(params.uniqueId.toString())
        .then(response => {
            setData({
                updated: true, success: response.data.success,
                description: response.data.description
            })
        })
        .catch(error => {
            setData({
                updated: true, success: false,
                description: "Server failure: " + error
            })
        })

    return (
        <div>
            {data.updated ? (
                <>
                    <h1>{data.success ? "Success" : "Failure"}</h1>
                    <p>{data.description}</p>

                    {data.success ? (<>
                        <Link to={'/login'}>Click to login!</Link>
                    </>):{}}
                </>
            ) : (<>
                <p>Loading...</p>
            </>)}
        </div>
    )
}
