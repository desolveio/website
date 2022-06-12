import React, {useState} from "react";
import ArtifactsAPI from "../../api/ArtifactsAPI";
import {isLoggedIn} from "axios-jwt";

export default function BuildArtifacts() {
    const [repository, setRepository] = useState("")
    const [response, setResponse] = useState("not uploaded")

    if (!isLoggedIn()) {
        return <div><h2>You must be logged in to view this page.</h2></div>
    }

    return (
        <div>
            <h2>Upload Artifacts</h2>
            <p>Build artifacts through our awesome system.</p>

            <p>Response: <b>{response}</b></p>

            <br></br>
            <label>GitHub Repository: </label>
            <input value={repository} onChange={e => setRepository(e.target.value)}/><br></br>

            <button onClick={()=> {
                setResponse("Building...")

                ArtifactsAPI.createArtifact(repository).then(result => {
                    setResponse(result.data.toString());
                });
            }}>Build</button>
        </div>
    )
}
