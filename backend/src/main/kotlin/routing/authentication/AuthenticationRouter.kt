package io.desolve.website.routing.authentication

import io.desolve.services.profiles.DesolveUserProfile
import io.desolve.website.authentication.JwtConfig
import io.desolve.website.authentication.login.LoginRequest
import io.desolve.website.authentication.registration.RegistrationRequest
import io.desolve.website.extensions.userProfile
import io.desolve.website.profileService
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.apache.commons.codec.digest.DigestUtils
import java.util.UUID

fun Route.routerAuth()
{
	route("auth")
	{
		// TODO: Potentially move these into their own classes/methods maybe?
		post("register") {
			val registration = this.call
				.receive<RegistrationRequest>()

			val user = this.call
				.userProfile()

			if (user != null)
			{
				this.call.respondText("Already logged in")
				return@post
			}

			val existing = profileService
				.findByEmail(registration.email)

			if (existing != null)
			{
				this.call.respondText("Account already exists")
				return@post
			}

			// TODO: 5/30/2022 additional checks for username
			val existingUsername = profileService
				.findByUsername(registration.username)

			if (existingUsername != null)
			{
				this.call.respondText("Account with username already exists")
				return@post
			}

			val sha256 = DigestUtils
				.sha256Hex(
					registration.password
				)

			val profile = DesolveUserProfile(
				uniqueId = UUID.randomUUID(),
				username = registration.username,
				email = registration.email,
				password = sha256
			)

			profileService.update(profile)
			this.call.respondText("Account created successfully")
		}

		post("login") {
			val credentials = this.call
				.receive<LoginRequest>()

			val user = profileService
				.findByEmail(credentials.email)

			if (user == null)
			{
				this.call.respond(mapOf("failure" to "invalid email"))
				return@post
			}

			val sha256 = DigestUtils
				.sha256Hex(
					credentials.password
				)

			if (user.password != sha256)
			{
				this.call.respond(mapOf("failure" to "invalid password"))
				return@post
			}

			// TODO: 5/29/2022 2FA on login?
			val token = JwtConfig.createToken(user)
			this.call.respondText(token)
		}

		authenticate(optional = true) {
			get("optional") {
				val user = this.call.userProfile()

				val response = if (user != null)
					"authenticated!" else "optional"

				this.call.respond(response)
			}
		}
	}
}
