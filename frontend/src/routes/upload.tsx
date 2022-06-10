import React, {useState} from "react";
import ArtifactsAPI from "../api/ArtifactsAPI";
import {isLoggedIn} from "axios-jwt";
import {useNavigate} from "react-router-dom";

export default function BuildArtifacts() {
    if (!isLoggedIn()) {
        const navigate = useNavigate()
        navigate('/404')
        return <div></div>
    }

    const [repository, setRepository] = useState("")
    const [groupId, setGroupId] = useState("")
    const [artifactId, setArtifactId] = useState("")
    const [version, setVersion] = useState("")

    const [response, setResponse] = useState("not uploaded")

    const submit = (event: React.FormEvent<HTMLFormElement>) => {
        setResponse("Building...")

        ArtifactsAPI.createArtifact(
            repository, groupId, artifactId, version
        ).then(result => {
            setResponse(result.data.toString());
        });
    }

    return (
        <div>
            <h1>Upload Artifacts</h1>
            <p>Build artifacts through our awesome system.</p>

            <br></br>
            <p>Response: <b>{response}</b></p>

            <form onSubmit={submit}>
                <br></br>
                <label>GitHub Repository: </label>
                <input type="url" value={repository} onChange={e => setRepository(e.target.value)}/><br></br>

                <label>Group ID: </label>
                <input type="text" value={groupId} onChange={e => setGroupId(e.target.value)}/><br></br>

                <label>Artifact ID: </label>
                <input type="text" value={artifactId} onChange={e => setArtifactId(e.target.value)}/><br></br>

                <label>Version: </label>
                <input type="text" value={version} onChange={e => setVersion(e.target.value)}/><br></br>

                <input type="submit" value="Perform Build Task"/>
            </form>
        </div>
    )
}
