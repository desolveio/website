import {useState} from "react";
import {useParams} from "react-router-dom";
import SetupAPI from "../../api/SetupAPI";

export default function SetupView() {
    const params = useParams()
    const [data, setBuildToolData] = useState("Loading...")

    // @ts-ignore
    SetupAPI.getBuildToolData(params.buildTool.toString())
        .then(data => {
            setBuildToolData(
                data.data.content
            )
        }, error => {
            setBuildToolData(
                "That build tool does not exist."
            )
        })

    return (
        <div>
            <h2>Repository Declaration:</h2>
            <p>{data}</p>
        </div>
    )
}
