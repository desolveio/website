package io.desolve.website.routing.authentication

import io.desolve.services.distcache.DesolveDistcacheService
import io.desolve.services.mail.DesolveMailService
import io.desolve.services.profiles.DesolveUserProfile
import io.desolve.services.profiles.DesolveUserProfileToken
import io.desolve.website.authentication.JwtConfig
import io.desolve.website.authentication.login.LoginRequest
import io.desolve.website.authentication.registration.RegistrationRequest
import io.desolve.website.development
import io.desolve.website.extensions.ensureUserProfile
import io.desolve.website.extensions.userProfile
import io.desolve.website.profileService
import io.desolve.website.regex.emailRegex
import io.desolve.website.regex.usernameRegex
import io.desolve.website.services.artifacts.DesolveArtifactContainer
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import jakarta.mail.Message
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

fun Route.routerAuth()
{
	route("auth")
	{
		@Serializable
		data class RegisterVerification(
			@Contextual val code: UUID
		)

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

			if (!emailRegex.matches(registration.email))
			{
				this.call.respondText("Invalid email supplied")
				return@post
			}

			if (
				registration.username.length > 16 || // https://imgur.com/a/VLWpNHE
				!usernameRegex.matches(registration.username)
			)
			{
				this.call.respondText("Invalid email supplied")
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

			val registrationId = UUID.randomUUID()

			DesolveDistcacheService
				.container<DesolveArtifactContainer>()
				.publishExpiringVerificationCode(
					registration.email.lowercase(),
					registrationId, profile
				)

			if (!development)
			{
				val parsed = DesolveMailService.parseTemplate(
					template = "email-verification.html",
					replacements = arrayOf(
						"user" to profile.username,
						"registerUrl" to "https://v1.desolve.io/register/verify/${registrationId}"
					)
				)

				DesolveMailService.sendEmail(
					subject = "[Desolve] Registration Confirmation", content = parsed,
					recipients = arrayOf(
						profile.email to Message.RecipientType.TO
					)
				)
			}

			this.call.respondText("Check your email for a verification code!")
		}

		post("register/verify") {
			val request = this.call
				.receive<RegisterVerification>()

			val container = DesolveDistcacheService
				.container<DesolveArtifactContainer>()

			if (!container.isValidCode(request.code))
			{
				return@post this.call.respond(mapOf(
					"success" to "false",
					"description" to "Invalid code provided!"
				))
			}

			val information = container
				.getInformation(request.code)

			profileService.update(information)
			container.invalidateCode(request.code)

			this.call.respond(mapOf(
				"success" to "true",
				"description" to "Navigate to login, account verified!"
			))
		}

		suspend fun DesolveUserProfile.updateRefreshToken()
		{
			refreshToken = DesolveUserProfileToken(UUID.randomUUID(), Instant.now().plusMillis(JwtConfig.refreshTokenValidity))
			profileService.updateRefreshToken(this, refreshToken)
		}

		@Serializable
		data class LoginOrRefreshSuccessResponse(val accessToken: String, @Contextual val refreshToken: DesolveUserProfileToken)

		post("refresh_token")
		{
			@Serializable
			data class RefreshTokenRequest(@Contextual val token: UUID)

			val request = call.receive<RefreshTokenRequest>()

			val profile = profileService.findByAccessToken(request.token)
			val refreshToken = profile?.refreshToken
			if (refreshToken == null)
			{
				call.respond(
					mapOf(
						"failure" to "invalid refresh token"
					)
				)
				return@post
			}

			if (Instant.now().isAfter(refreshToken.expiration))
			{
				call.respond(
					mapOf(
						"failure" to "expired refresh token"
					)
				)
				return@post
			}

			val token = JwtConfig.createToken(profile)
			profile.updateRefreshToken()

			call.respond(LoginOrRefreshSuccessResponse(token, profile.refreshToken!!))
		}

		post("login") {
			val credentials = this.call
				.receive<LoginRequest>()

			val user = profileService
				.findByEmail(credentials.email)

			if (user == null)
			{
				this.call.respond(mapOf("description" to "invalid email"))
				return@post
			}

			val sha256 = DigestUtils
				.sha256Hex(
					credentials.password
				)

			if (user.password != sha256)
			{
				this.call.respond(mapOf("description" to "invalid password"))
				return@post
			}

			// TODO: 5/29/2022 2FA on login?
			val token = JwtConfig.createToken(user)

			val refreshToken = DesolveUserProfileToken(UUID.randomUUID(), Instant.now().plus(7, ChronoUnit.DAYS))
			profileService.updateRefreshToken(user, refreshToken)

			this.call.respond(LoginOrRefreshSuccessResponse(token, refreshToken))
		}

		authenticate {
			get("logout") {
				val profile = this.call.ensureUserProfile()
				profileService.updateRefreshToken(profile, null)
				call.respond(mapOf("success" to "Successfully logged out"))
			}
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
