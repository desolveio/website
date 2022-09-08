package io.desolve.website.routing.setup

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import io.ktor.server.routing.*

/**
 * @author GrowlyX
 * @since 6/12/2022
 */
fun Route.routerSetup()
{
    val mappings = mapOf(
        "gradle" to """
           allprojects {
           	repositories {
           		...
           		maven { url "https://desolve.io/repository" }
           	}
           }
        """.trimIndent(),
        "maven" to """
            <repositories>
            	<repository>
            	    <id>desolve.io</id>
            	    <url>https://desolve.io/repository</url>
            	</repository>
            </repositories>
        """.trimIndent(),
        "sbt" to """
            resolvers += "desolve" at "https://desolve.io/repository"
        """.trimIndent(),
    )

    @Serializable
    data class SetupRequest(val buildTool: String)

    post("repoDeclaration") {
        val request = this.call
            .receive<SetupRequest>()

        val mapping = mappings[
            request.buildTool.lowercase()
        ]

        if (mapping == null)
        {
            this.call.respond(mapOf(
                "content" to "That build tool does not exist."
            ))
            return@post
        }

        this.call.respond(mapOf(
            "content" to mapping
        ))
    }
}
