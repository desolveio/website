import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class SetupAPI {

    getRepoDeclaration(buildTool: String): Promise<AxiosResponse> {
        return axios.post('setup/repoDeclaration', {buildTool: buildTool})
    }

}

export default new SetupAPI();
