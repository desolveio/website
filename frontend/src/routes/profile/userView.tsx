import {useState} from "react";
import {useParams} from "react-router-dom";
import ProfileAPI from "../../api/ProfileAPI";

export default function UserView() {
    const params = useParams()
    const [data, setUserData] = useState("???")

    // @ts-ignore
    ProfileAPI.grabInformationOf(params.username.toString())
        .then(data => {
            setUserData(
                JSON.stringify(data.data)
            )
        }, error => {
            setUserData(
                "failed to grab user data: " + error.toString()
            )
        })

    return (
        <div>
            <h1>{params.username}</h1>{" "}
            <img src={"https://github.com/" + params.username + ".png?size=100"} alt={"hi"}/>

            <p>Viewing awesome info:</p>
            <p>{data}</p>
        </div>
    )
}
