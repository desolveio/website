package io.desolve.website.routing.artifacts

import io.desolve.services.distcache.DesolveDistcacheService
import io.desolve.services.protocol.ArtifactLookupRequest
import io.desolve.website.services.ClientService
import io.desolve.website.services.artifacts.DesolveArtifactContainer
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * @author GrowlyX
 * @since 6/5/2022
 */
fun Route.routerArtifactsAuthenticated()
{
    get("storageStatus/{artifactId}")
    {
        val artifactId = call
            .parameters["artifactId"]

        if (artifactId.isNullOrBlank())
        {
            call.respond(mapOf(
                "description" to "invalid artifactId"
            ))
            return@get
        }

        val location = DesolveDistcacheService
            .container<DesolveArtifactContainer>()
            .findArtifactLocation(artifactId)

        if (location == null)
        {
            call.respond(mapOf(
                "description" to "seems like this artifacts lost woo"
            ))
            return@get
        }

        val request = ArtifactLookupRequest
            .newBuilder()
            .setArtifactUniqueId(artifactId)
            .setArtifactServerId(location)
            .build()

        val response = ClientService
            .artifactClient.stub()
            .lookupArtifact(request)

        // TODO: 6/5/2022 do something cool with response
    }
}
