import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class ArtifactsAPI {

    createArtifact(
        repository: String, groupId: String,
        artifactId: String, version: String
    ): Promise<AxiosResponse> {
        return axios.post('artifacts/createArtifact', {
            repository: repository, dependency: `${groupId}:${artifactId}:${version}`
        })
    }

}

export default new ArtifactsAPI();
