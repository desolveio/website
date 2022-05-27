package io.desolve.website

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.vue
import io.ktor.server.locations.Locations
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing

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

	routing {
		configureSPA()
	}
}

private fun Application.configureRouting()
{
	install(Locations)

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
		.toBooleanStrictOrNull()

	if (development == null || development == true)
	{
		println("Skipping SPA configuration as a development environment has been detected ($development).")
		return
	}

	// configure KTor to serve Vue content
	singlePageApplication {
		react("public")
		useResources = true
	}
}
