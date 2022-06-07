import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class ProfileAPI {

    grabInformation(): Promise<AxiosResponse> {
        return axios.get('user/information')
    }

    grabInformationOf(user: String): Promise<AxiosResponse> {
        return axios.get("user/information/view/" + user)
    }

}

export default new ProfileAPI();
