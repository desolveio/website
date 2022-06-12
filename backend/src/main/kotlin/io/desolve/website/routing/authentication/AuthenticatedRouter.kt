package io.desolve.website.routing.authentication

import io.desolve.website.routing.artifacts.routerArtifacts
import io.desolve.website.routing.profile.routerProfileAuthenticated
import io.ktor.server.routing.*

/**
 * @author GrowlyX
 * @since 6/5/2022
 */
fun Route.routerAuthenticated()
{
    route("user")
    {
        routerProfileAuthenticated()
    }

    route("artifacts")
    {
        routerArtifacts()
    }
}
