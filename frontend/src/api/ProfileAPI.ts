import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class ProfileAPI {

    grabInformation(): Promise<AxiosResponse> {
        return axios.get('user/information')
    }

}

export default new ProfileAPI();
