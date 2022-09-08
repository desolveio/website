import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class ProfileAPI {

    getData(): Promise<AxiosResponse> {
        return axios.get('user/data')
    }

    getDataOf(user: String): Promise<AxiosResponse> {
        return axios.get("user/data/view/" + user)
    }

}

export default new ProfileAPI();
