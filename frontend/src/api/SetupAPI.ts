import axios from '../utils/AxiosUtil'
import {AxiosResponse} from "axios"

class SetupAPI {

    getBuildToolData(buildTool: String): Promise<AxiosResponse> {
        return axios.post('setup/setupData', {buildTool: buildTool})
    }

}

export default new SetupAPI();
