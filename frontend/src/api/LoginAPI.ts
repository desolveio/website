import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios";

class LoginAPI {

    submitLogin(email: String, password: String): Promise<AxiosResponse<String>> {
        return axios.post('login', {email: email, password: password})
    }

}

export default new LoginAPI()