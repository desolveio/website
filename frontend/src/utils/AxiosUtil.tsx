import axios from 'axios'
import {applyAuthTokenInterceptor, clearAuthTokens, IAuthTokens, TokenRefreshRequest} from "axios-jwt";
import {useNavigate} from "react-router-dom";
import {useState} from "react";

const BASE_URL = '/api/'

let axiosSetup = false;

const axiosInstance = axios.create({
    baseURL: BASE_URL,
    timeout: 1500,
    headers: {
        'Content-Type': 'application/json'
    }
})

export function SetupAxios() {
    const [setup, setSetup] = useState(false)
    let navigation = useNavigate()

    if (setup)
        return (<></>)

    setSetup(true)

    const requestRefresh: TokenRefreshRequest = async (refreshToken: string): Promise<IAuthTokens | string> => {
        const response = await axios.post(`${BASE_URL}/auth/refresh_token`, {token: refreshToken})

        if (response.data.failure) {
            console.log("Refresh token failed, logging out.")
            clearAuthTokens()
            navigation('/login')
        } else {
            console.log("Refreshed access token")
        }

        return {
            accessToken: response.data.accessToken,
            refreshToken: response.data.refreshToken.token
        }
    }

    applyAuthTokenInterceptor(axiosInstance, {
        requestRefresh,  // async function that takes a refreshToken and returns a promise the resolves in a fresh accessToken
        header: "Authorization",  // header name
        headerPrefix: "Bearer ",  // header value prefix
    })

    return (
        <>
        </>
    )
}

export default axiosInstance
