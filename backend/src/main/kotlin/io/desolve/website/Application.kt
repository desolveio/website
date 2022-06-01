package io.desolve.website

import io.desolve.services.profiles.DesolveUserProfilePlatformTools
import io.desolve.website.authentication.JwtConfig
import io.desolve.website.routing.router
import io.desolve.website.utils.desolveJson
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.locations.Locations
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import java.net.URL
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger

fun main()
{
	// suppress mongodb logging like I want to suppress the headaches growly gives me
	Logger.getLogger("org.mongodb.driver").level = Level.SEVERE

	embeddedServer(
		Netty,
		host = "0.0.0.0",
		port = 8080,
		watchPaths = listOf("classes", "resources")
	) {
		routing()

		println("Started website on http://localhost:8080")
	}.start(wait = true)
}

fun Application.routing()
{
	configureRouting()
	configureAuthentication()

	routing {
		configureSPA()
	}
}

val profileService by lazy {
	DesolveUserProfilePlatformTools.service()
}

fun Application.configureAuthentication()
{
	install(Authentication) {
		jwt {
			verifier(JwtConfig.verifier)
			this.realm = "desolve.io"

			validate {
				val uniqueId = it.payload
					.getClaim("uniqueId")
					.asString()
					?: return@validate null

				return@validate profileService
					.findByUniqueId(
						UUID.fromString(uniqueId)
					)
			}
		}
	}
}

private fun Application.configureRouting()
{
	install(Locations)
	install(ContentNegotiation) {
		json(
			json = desolveJson
		)
	}

	this.router()

	val betterStatusCodes = System
		.getProperty("io.desolve.website.status")
		?.toBoolean() ?: false

	install(StatusPages) {
		if (betterStatusCodes)
		{
			val cat404 = URL("https://http.cat/404")
				.readBytes()

			status(HttpStatusCode.NotFound) { call, _ ->
				call.respondBytes(
					status = HttpStatusCode.NotFound,
					bytes = cat404
				)
			}

			return@install
		}

		status(HttpStatusCode.NotFound) { call, _ ->
			call.respond(mapOf(
				"description" to "404 Not Found"
			))
		}

		status(HttpStatusCode.Unauthorized) { call, _ ->
			call.respond(mapOf(
				"description" to "401 Unauthorized"
			))
		}
	}
}

private fun Routing.configureSPA()
{
	// if we are in development, we don't want to serve SPA
	// with SPA enabled, we can't see 404s from the API with ease
	// as it redirects to the SPA
	val development = System
		.getProperty("io.ktor.development")
		?.toBoolean() ?: false

	if (development)
	{
		println("Skipping SPA configuration as a development environment has been detected.")
		return
	}

	// configure KTor to serve Vue content
	singlePageApplication {
		react("static")
		useResources = true
	}
}
