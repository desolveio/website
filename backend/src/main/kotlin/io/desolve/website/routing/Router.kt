package io.desolve.website.routing

import io.desolve.website.routing.authentication.routerAuthenticated
import io.desolve.website.routing.authentication.routerAuth
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.router()
{
	routing {
		route("api")
		{
			routerAuth()

			authenticate {
				routerAuthenticated()
			}
		}
	}
}
