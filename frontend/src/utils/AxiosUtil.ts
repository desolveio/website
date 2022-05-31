import axios from 'axios'
import {applyAuthTokenInterceptor, IAuthTokens, TokenRefreshRequest} from "axios-jwt";

const BASE_URL = '/api/'

const axiosInstance = axios.create({
    baseURL: BASE_URL,
    timeout: 1500,
    headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
    }
})

const requestRefresh: TokenRefreshRequest = async (refreshToken: string): Promise<IAuthTokens | string> => {

    // Important! Do NOT use the axios instance that you supplied to applyAuthTokenInterceptor (in our case 'axiosInstance')
    // because this will result in an infinite loop when trying to refresh the token.
    // Use the global axios client or a different instance
    const response = await axios.post(`${BASE_URL}/auth/refresh_token`, {token: refreshToken})

    // If your backend supports rotating refresh tokens, you may also choose to return an object containing both tokens:
    // return {
    //  accessToken: response.data.access_token,
    //  refreshToken: response.data.refresh_token
    //}

    return response.data.access_token
}

applyAuthTokenInterceptor(axiosInstance, {
    requestRefresh,  // async function that takes a refreshToken and returns a promise the resolves in a fresh accessToken
    header: "Authorization",  // header name
    headerPrefix: "Bearer ",  // header value prefix
})

export default axiosInstance
