package io.desolve.website

import io.desolve.services.profiles.DesolveUserProfileService
import io.desolve.website.authentication.JwtConfig
import io.desolve.website.extensions.userProfile
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.locations.Locations
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.util.UUID

fun main()
{
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

private val profileService =
	DesolveUserProfileService()

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
	install(ContentNegotiation)

	routing {
		// TODO: Relocate the contents of routing to their own files/packages, this is temporary for now
		route("api")
		{
			@kotlinx.serialization.Serializable
			class LoginRequest(val email: String, val password: String)

			// TODO: 5/28/2022 logic for login & authenticated routing
			//  https://github.com/AndreasVolkmann/ktor-auth-jwt-sample/blob/master/src/main/kotlin/me/avo/io/ktor/auth/jwt/sample/Module.kt
			post("login") {
				val credentials = call.receive<LoginRequest>()
				val user = profileService.findByEmail(credentials.email)
				if (user == null)
				{
					call.respondText { "invalid email" }
					return@post
				}
				// todo: password check?
				val token = JwtConfig.createToken(user)
				call.respondText(token)
			}

			authenticate(optional = true) {
				get("optional") {
					val user = call.userProfile()
					val response = if (user != null) "authenticated!" else "optional"
					call.respond(response)
				}
			}
		}
	}


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
		.toBooleanStrictOrNull() ?: false

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
