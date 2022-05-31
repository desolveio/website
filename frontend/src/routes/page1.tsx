import AuthenticationAPI from "../api/AuthenticationAPI";

export default function Page1() {
    return (
        <div>
            <h1>Page 1</h1>
            <p>Page one contents are soooo cool!</p>

            <button onClick={ () => {
                AuthenticationAPI.testAuthentication()
                    .then(test => {
                        console.log(`Test result = ${test.data}`)
                    }, error => {
                        console.log(`Test error = ${error}`)
                    })
            }}>Test Optional Auth</button>
        </div>
    )
}