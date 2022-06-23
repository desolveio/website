package io.desolve.website.routing.artifacts

import io.desolve.services.artifacts.dao.DesolveStoredArtifact
import io.desolve.services.artifacts.dao.DesolveStoredArtifactService
import io.desolve.services.artifacts.dao.DesolveStoredProject
import io.desolve.services.artifacts.dao.DesolveStoredProjectService
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
    val service = DesolveStoredArtifactService()
    val projectService = DesolveStoredProjectService()

    @Serializable
    class Repository(val repository: String)

    @Serializable
    @Suppress("unused")
    class ArtifactMetrics(
        val downloads: List<Long>,
        val firstDownload: Long?,
        val lastDownload: Long?
    )

    post("createArtifact")
    {
        val creation = this.call
            .receive<Repository>()

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

    @Serializable
    data class ProjectData(
        val project: DesolveStoredProject,
        val associated: List<DesolveStoredArtifact>
    )

    post("projectData")
    {
        val request = call
            .receive<Repository>()

        val project = projectService
            .byRepository(
                request.repository.lowercase()
            )
            ?: return@post call.respond(
                mapOf(
                    "description" to "invalid repository/no project found"
                )
            )

        val artifacts =
            mutableListOf<DesolveStoredArtifact>()

        for (artifact in project.associated)
        {
            service.findByUniqueId(artifact)
                ?.apply { artifacts += this }
        }

        val response = ProjectData(
            project = project,
            associated = artifacts
        )

        call.respond(response)
    }

    get("metrics/{artifactId}")
    {
        val artifactId = this.call
            .parseArtifactId()
            ?: return@get

        val model = service
            .findByUniqueId(
                UUID.fromString(artifactId)
            )
            ?: return@get call.respond(
                mapOf(
                    "description" to "no model found for artifactId"
                )
            )

        // easier for other languages to work with our Instants
        val metrics = ArtifactMetrics(
            model.metrics.downloads.map { it.toEpochMilli() },
            model.metrics.firstDownloaded?.toEpochMilli(),
            model.metrics.lastDownloaded?.toEpochMilli()
        )

        call.respond(metrics)
    }

    get("basicLookup/{artifactId}")
    {
        val artifactId = this.call
            .parseArtifactId()
            ?: return@get

        val location = DesolveDistcacheService
            .container<DesolveArtifactContainer>()
            .findArtifactLocation(artifactId)

        if (location == null)
        {
            call.respond(
                mapOf(
                    "description" to "seems like this artifacts lost woo"
                )
            )
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
            ArtifactLookupResult.NOT_FOUND ->
            {
                call.respond(
                    mapOf(
                        "description" to "seems like this artifacts lost woo"
                    )
                )
            }

            ArtifactLookupResult.UNRECOGNIZED ->
            {
                call.respond(
                    mapOf(
                        "description" to "did not recognize this artifact"
                    )
                )
            }

            ArtifactLookupResult.EXISTS ->
            {
                call.respond(
                    mapOf(
                        "description" to "found",
                        "location" to location,
                        "contentExists" to response
                            .contentMap.isNotEmpty().toString()
                    )
                )
            }

            else ->
            {
                call.respond(
                    mapOf(
                        "description" to "no result was returned"
                    )
                )
            }
        }
    }
}

suspend fun ApplicationCall.parseArtifactId(): String?
{
    val artifactId = parameters["artifactId"]

    if (artifactId.isNullOrBlank())
    {
        respond(
            mapOf(
                "description" to "invalid artifactId"
            )
        )
        return null
    }

    val uniqueId = kotlin
        .runCatching {
            UUID.fromString(artifactId)
        }
        .getOrNull()


    if (uniqueId == null)
    {
        respond(
            mapOf(
                "description" to "invalid artifactId"
            )
        )
        return null
    }

    return artifactId
}
