import React, {useState} from "react";
import ArtifactsAPI from "../../api/ArtifactsAPI";

export default function BuildArtifacts() {
    const [repository, setRepository] = useState("")
    const [response, setResponse] = useState("not uploaded")

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
