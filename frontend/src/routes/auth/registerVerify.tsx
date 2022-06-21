import React, {useEffect, useState} from "react";
import AuthenticationAPI from "../../api/AuthenticationAPI";
import {Link, useParams} from "react-router-dom";

export default function RegisterVerify() {
    const [state, setState] = useState({
        updated: false, success: false, description: ""
    })

    let {uniqueId} = useParams();

    useEffect(() => {
        AuthenticationAPI
            .submitRegistrationVerification(
                (uniqueId as any).toString()
            )
            .then(response => {
                setState({
                    updated: true, success: response.data.success,
                    description: response.data.description
                })
            })
            .catch(error => {
                setState({
                    updated: true, success: false,
                    description: "Server failure: " + error
                })
            })
    }, [uniqueId])

    return (
        <div>
            {state.updated ? (
                <>
                    <h1>{state.success ? "Success" : "Failure"}</h1>
                    <p>{state.description}</p>

                    {state.success ? (<>
                        <Link to={'/login'}>Click to login!</Link>
                    </>) : (
                        <></>
                    )}
                </>
            ) : (<>
                <p>Loading...</p>
            </>)}
        </div>
    )
}
