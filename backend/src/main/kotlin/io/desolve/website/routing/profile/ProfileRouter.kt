package io.desolve.website.routing.profile

import io.desolve.services.profiles.DesolveUserProfilePlatformTools
import io.desolve.website.extensions.ensureUserProfile
import io.desolve.website.utils.desolveJson
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString

/**
 * @author GrowlyX
 * @since 6/6/2022
 */
fun Route.routerProfileAuthenticated()
{
    get("data")
    {
        val profile = this.call.ensureUserProfile()
        call.respond(profile)
    }
}

fun Route.routerProfile()
{
    get("data/view/{username}")
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

        // sanitize the DB model and only return public info
        call.respond(mapOf(
            "uniqueId" to profile.uniqueId.toString(),
            "username" to profile.username,
            "assets" to desolveJson.encodeToString(profile.assets)
        ))
    }
}
