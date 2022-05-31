package io.desolve.website

import io.desolve.services.profiles.DesolveUserProfileService
import io.desolve.website.authentication.JwtConfig
import io.desolve.website.routing.router
import io.desolve.website.utils.desolveJson
import io.ktor.http.HttpStatusCode
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
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import org.litote.kmongo.serialization.SerializationClassMappingTypeService
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger

fun main()
{
	// do not remove
	System.setProperty(
		"org.litote.mongo.mapping.service",
		SerializationClassMappingTypeService::class
			.qualifiedName!!
	)

	// suppress mongodb logging like I want to suppress the headaches growly gives me
	Logger.getLogger("org.mongodb.driver").level = Level.SEVERE

	embeddedServer(
		Netty,
		host = "0.0.0.0",
		port = 8080,
		watchPaths = listOf("classes", "resources")
	) {
		routing()
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
	DesolveUserProfileService()
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

	install(StatusPages) {
		status(HttpStatusCode.NotFound) { call, _ ->
			call.respondText { "404" }
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
		react("public")
		useResources = true
	}
}
