import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class AuthenticationAPI {

    submitLogin(email: String, password: String): Promise<AxiosResponse<String>> {
        return axios.post('auth/login', {email: email, password: password})
    }

    submitRegistration(email: String, username: String, password: String): Promise<AxiosResponse<String>> {
        return axios.post('auth/register', {email: email, username: username, password: password})
    }

    testAuthentication(): Promise<AxiosResponse<String>> {
        return axios.get('auth/optional')
    }

}

export default new AuthenticationAPI();
