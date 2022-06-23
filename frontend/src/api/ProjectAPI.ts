import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class ProjectAPI {

    projectData(repository: String): Promise<AxiosResponse> {
        return axios.post('artifacts/projectData', {repository: repository})
    }

}

export default new ProjectAPI();
