import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import ProfileAPI from "../../api/ProfileAPI";

export default function UserView() {
    const {username} = useParams()

    const [data, setUserData] =
        useState("Loading...")

    useEffect(() => {
        // @ts-ignore
        ProfileAPI.grabInformationOf(username.toString())
            .then(data => {
                setUserData(
                    JSON.stringify(data.data)
                )
            }, error => {
                setUserData(
                    "failed to grab user data: " + error.toString()
                )
            })
    }, [username])

    return (
        <div>
            <h1>{username}</h1>{" "}
            <img src={`https://github.com/${username}.png?size=100`} alt={"hi"}/>

            <p>Viewing awesome info:</p>
            <p>{data}</p>
        </div>
    )
}
