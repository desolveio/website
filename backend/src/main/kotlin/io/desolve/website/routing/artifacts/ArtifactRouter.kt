package io.desolve.website.routing.artifacts

import io.desolve.services.distcache.DesolveDistcacheService
import io.desolve.services.protocol.ArtifactLookupRequest
import io.desolve.services.protocol.ArtifactLookupResult
import io.desolve.services.protocol.DependencyLocation
import io.desolve.services.protocol.WorkerRequest
import io.desolve.website.services.ClientService
import io.desolve.website.services.artifacts.DesolveArtifactContainer
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

/**
 * @author GrowlyX
 * @since 6/5/2022
 */
fun Route.routerArtifacts()
{
    @Serializable
    class ArtifactCreate(val repository: String)

    post("createArtifact")
    {
        val creation = this.call
            .receive<ArtifactCreate>()

        val uniqueId = UUID.randomUUID().toString()

        val request = WorkerRequest.newBuilder()
            .setArtifactUniqueId(uniqueId)
            .setSpecification(
                DependencyLocation.newBuilder()
                    .setRepositoryUrl(creation.repository)
                    .build()
            )
            .setTimestamp(
                System.currentTimeMillis().toString()
            )
            .build()

        call.respond(
            ClientService.workerClient.stub()
                .startTaskWork(request)
                .status.name
        )
    }

    get("basicLookup/{artifactId}")
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

        return@get when (response.result)
        {
            ArtifactLookupResult.NOT_FOUND -> {
                call.respond(mapOf(
                    "description" to "seems like this artifacts lost woo"
                ))
            }
            ArtifactLookupResult.UNRECOGNIZED -> {
                call.respond(mapOf(
                    "description" to "did not recognize this artifact"
                ))
            }
            ArtifactLookupResult.EXISTS -> {
                call.respond(mapOf(
                    "description" to "found",
                    "location" to location,
                    "contentExists" to response
                        .contentMap.isNotEmpty().toString()
                ))
            }
            else -> {
                call.respond(mapOf(
                    "description" to "no result was returned"
                ))
            }
        }
    }
}
