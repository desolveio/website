package io.desolve.website.routing.profile

import io.desolve.services.profiles.DesolveUserProfilePlatformTools
import io.desolve.website.extensions.ensureUserProfile
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * @author GrowlyX
 * @since 6/6/2022
 */
fun Route.routerProfileAuthenticated()
{
    get("information")
    {
        val profile = this.call.ensureUserProfile()
        call.respond(profile)
    }

    get("informationGrabLatest")
    {
        val profile = this.call
            .ensureUserProfile()
            .uniqueId

        call.respond(
            DesolveUserProfilePlatformTools
                .service().findByUniqueId(profile)!!
        )
    }
}
