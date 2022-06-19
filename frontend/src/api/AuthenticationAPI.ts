import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class AuthenticationAPI {

    submitLogin(email: String, password: String): Promise<AxiosResponse> {
        return axios.post('auth/login', {email: email, password: password})
    }

    submitRegistration(email: String, username: String, password: String): Promise<AxiosResponse> {
        return axios.post('auth/register', {email: email, username: username, password: password})
    }

    submitRegistrationVerification(code: string): Promise<AxiosResponse> {
        return axios.post('/auth/register/verify', {code: code})
    }

    submitLogout(): Promise<AxiosResponse> {
        return axios.get('auth/logout')
    }

    testAuthentication(): Promise<AxiosResponse<String>> {
        return axios.get('auth/optional')
    }

}

export default new AuthenticationAPI();
