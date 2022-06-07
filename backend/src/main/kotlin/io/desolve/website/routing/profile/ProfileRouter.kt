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
    route("information")
    {
        get("")
        {
            val profile = this.call.ensureUserProfile()
            call.respond(profile)
        }

        get("sync")
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
}

fun Route.routerProfile()
{
    route("information")
    {
        get("view/{username}")
        {
            val username = call
                .parameters["username"]
                ?: return@get call.respond(mapOf(
                    "description" to "no username provided"
                ))

            // TODO: 6/6/2022 case different usernames
            //  won't be matched with KMongo using eq
            val profile = DesolveUserProfilePlatformTools
                .service().findByUsername(username)
                ?: return@get call.respond(mapOf(
                    "description" to "no account found"
                ))

            call.respond(profile)
        }
    }
}
