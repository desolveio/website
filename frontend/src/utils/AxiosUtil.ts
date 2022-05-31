import axios from 'axios'

export default axios.create({
    baseURL: `/api/`,
    timeout: 1500,
    headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*'
        // todo: append token here
        // 'Authorization': `Bearer ${""}`
    }
})
