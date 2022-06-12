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
           		maven { url "https://v1.desolve.io/repository" }
           	}
           }
        """.trimIndent(),
        "maven" to """
            <repositories>
            	<repository>
            	    <id>desolve.io</id>
            	    <url>https://v1.desolve.io/repository</url>
            	</repository>
            </repositories>
        """.trimIndent(),
        "sbt" to """
            resolvers += "desolve" at "https://v1.desolve.io/repository"
        """.trimIndent(),
    )

    @Serializable
    data class SetupRequest(val buildTool: String)

    post("setupData") {
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
