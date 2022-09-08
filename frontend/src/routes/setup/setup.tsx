import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import SetupAPI from "../../api/SetupAPI";

export default function SetupView() {
    const {buildTool} = useParams()
    const [data, setBuildToolData] =
        useState("Loading...")

    useEffect(() => {
        // @ts-ignore
        SetupAPI.getRepoDeclaration(buildTool.toString())
            .then(data => {
                setBuildToolData(
                    data.data.content
                )
            }, error => {
                setBuildToolData(
                    "That build tool does not exist."
                )
            })
    }, [buildTool])

    return (
        <div>
            <h2>Repository Declaration:</h2>
            <p>{data}</p>
        </div>
    )
}
