import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios";

class RegisterAPI {

    submitRegistration(email: String, username: String, password: String): Promise<AxiosResponse<String>> {
        return axios.post('register', {email: email, username: username, password: password})
    }

}

export default new RegisterAPI()
