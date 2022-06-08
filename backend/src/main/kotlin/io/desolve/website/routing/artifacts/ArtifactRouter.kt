package io.desolve.website.routing.artifacts

import io.desolve.services.distcache.DesolveDistcacheService
import io.desolve.services.protocol.*
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
    class ArtifactCreate(val repository: String, val dependency: String)

    post("createArtifact")
    {
        val creation = this.call
            .receive<ArtifactCreate>()

        val split = creation.dependency
            .split(":")

        val uniqueId = UUID.randomUUID().toString()

        val request = WorkerRequest.newBuilder()
            .setArtifactUniqueId(uniqueId)
            // TODO: 6/7/2022 detect lol
            .setBuildTool(BuildTool.GRADLE)
            .setSpecification(
                DependencyLocation.newBuilder()
                    .setRepositoryUrl(creation.repository)
                    .setGroupId(split[1])
                    .setArtifactId(split[1])
                    .setVersion(split[2])
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
