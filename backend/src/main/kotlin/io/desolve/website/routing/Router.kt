package io.desolve.website.routing

import io.desolve.website.routing.artifacts.routerArtifactsAnon
import io.desolve.website.routing.authentication.routerAuth
import io.desolve.website.routing.authentication.routerAuthenticated
import io.desolve.website.routing.profile.routerProfile
import io.desolve.website.routing.setup.routerSetup
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.micrometer.prometheus.PrometheusMeterRegistry

fun Application.router(registry: PrometheusMeterRegistry)
{
	routing {
		route("api")
		{
			routerAuth()

			route("user") {
				routerProfile()
			}

			route("setup") {
				routerSetup()
			}

			route("artifacts") {
				routerArtifactsAnon()
			}

			authenticate {
				routerAuthenticated()
			}
		}
	}
}
