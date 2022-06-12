package io.desolve.website.routing

import com.google.common.cache.CacheBuilder
import io.desolve.website.routing.artifacts.routerArtifacts
import io.desolve.website.routing.authentication.routerAuthenticated
import io.desolve.website.routing.authentication.routerAuth
import io.desolve.website.routing.profile.routerProfile
import io.desolve.website.routing.setup.routerSetup
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.router()
{
	// TODO: 6/6/2022 rater limiting?
	val rateLimit = CacheBuilder.newBuilder()
		.build<String, Int>()

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

			authenticate {
				routerAuthenticated()
			}
		}
	}
}
