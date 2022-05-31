import axios from 'axios'
import {applyAuthTokenInterceptor, clearAuthTokens, IAuthTokens, TokenRefreshRequest} from "axios-jwt";

const BASE_URL = '/api/'

const axiosInstance = axios.create({
    baseURL: BASE_URL,
    timeout: 1500,
    headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
    }
})

// TODO: This runs on every web request, we might need to make it check the expiration time or something
const requestRefresh: TokenRefreshRequest = async (refreshToken: string): Promise<IAuthTokens | string> => {
    const response = await axios.post(`${BASE_URL}/auth/refresh_token`, {token: refreshToken})

    if (response.data.failure) {
        console.log("Refresh token failed, logging out.")
        clearAuthTokens()
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

export default axiosInstance
