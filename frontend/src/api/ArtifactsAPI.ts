import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class ArtifactsAPI {

    createArtifact(repository: String): Promise<AxiosResponse> {
        return axios.post('artifacts/createArtifact', {repository: repository})
    }

}

export default new ArtifactsAPI();
