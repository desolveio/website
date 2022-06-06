package io.desolve.website.routing.authentication

import io.desolve.website.routing.artifacts.routerArtifactsAuthenticated
import io.ktor.server.routing.*

/**
 * @author GrowlyX
 * @since 6/5/2022
 */
fun Route.routerAuthenticated()
{
    route("artifacts")
    {
        routerArtifactsAuthenticated()
    }
}
