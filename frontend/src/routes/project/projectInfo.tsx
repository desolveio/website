import React, {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import ProjectAPI from "../../api/ProjectAPI";

export default function ProjectInfo() {
    const [state, setState] = useState({
        updated: false, associated: [], description: ""
    })

    let { userId, projectName } = useParams();

    useEffect(() => {
        ProjectAPI
            // TODO: allow diff git providers
            .projectData(`https://github.com/${userId}/${projectName}`)
            .then(response => {
                setState({
                    updated: true, associated: response.data.project.associated,
                    description: response.data.description
                })
            })
            .catch(error => {
                setState({
                    updated: true, associated: [],
                    description: "Server failure: " + error
                })
            })
    }, [userId, projectName])

    return (
        <div>
            {state.updated ? (
                <>
                    <h1>{state.description == null ? "Viewing project info for: " + `${userId}/${projectName}` : "Not found"}</h1>

                    {state.description == null ? (<>
                        <p>{JSON.stringify(state.associated)}</p>
                    </>) : (
                        <p>This project was not found.</p>
                    )}
                </>
            ) : (<>
                <h1>Please wait</h1>
                <p>The content is loading.</p>
            </>)}
        </div>
    )
}
