package io.desolve.website.routing.artifacts

import io.desolve.services.distcache.DesolveDistcacheService
import io.desolve.services.protocol.ArtifactLookupRequest
import io.desolve.services.protocol.ArtifactLookupResult
import io.desolve.services.protocol.BuildTool
import io.desolve.services.protocol.GitSpecification
import io.desolve.services.protocol.WorkerRequest
import io.desolve.website.services.ClientService
import io.desolve.website.services.artifacts.DesolveArtifactContainer
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

/**
 * @author GrowlyX
 * @since 6/5/2022
 */
fun Route.routerArtifacts()
{
    @Serializable
    class ArtifactCreate(val repository: String, val dependency: String)

    post("createArtifact")
    {
        val creation = this.call
            .receive<ArtifactCreate>()

        val split = creation.dependency
            .split(":")

        val request = WorkerRequest.newBuilder()
            .setArtifactUniqueId(
                UUID.randomUUID().toString()
            )
            // TODO: 6/7/2022 detect lol
            .setBuildTool(BuildTool.GRADLE)
            .setSpecification(
                GitSpecification.newBuilder()
                    .setRepositoryUrl(creation.repository)
                    .build()
            )
            .setTimestamp(
                System.currentTimeMillis().toString()
            )
            .build()

        ClientService.workerClient.stub()
            .startTaskWork(request)
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
                        .contentMap.isNotEmpty()
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
