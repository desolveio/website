import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class ArtifactsAPI {

    createArtifact(repository: String): Promise<AxiosResponse> {
        return axios.post('artifacts/createArtifact', {repository: repository})
    }

    metrics(artifactId: String): Promise<AxiosResponse> {
        return axios.get(`artifacts/metrics/${artifactId}`)
    }

}

export default new ArtifactsAPI();
