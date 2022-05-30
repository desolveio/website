import axios from 'axios'

export default axios.create({
    baseURL: `/api/`,
    timeout: 1000,
    headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
    }
})