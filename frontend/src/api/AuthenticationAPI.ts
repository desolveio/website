import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class AuthenticationAPI {

    submitLogin(email: String, password: String): Promise<AxiosResponse<String>> {
        return axios.post('login', {email: email, password: password})
    }

    submitRegistration(email: String, username: String, password: String): Promise<AxiosResponse<String>> {
        return axios.post('register', {email: email, username: username, password: password})
    }

}

export default new AuthenticationAPI();