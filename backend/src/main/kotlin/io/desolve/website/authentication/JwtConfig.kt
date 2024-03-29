package io.desolve.website.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.desolve.services.profiles.DesolveUserProfile
import java.time.Duration
import java.util.Date

/**
 * @author GrowlyX
 * @since 5/28/2022
 */
object JwtConfig
{

	private const val secret = "desolve-test"
	private const val issuer = "desolve.io"

	private val validity = Duration
		.ofMinutes(15L)
		.toMillis()

	val refreshTokenValidity = Duration
		.ofDays(7)
		.toMillis()

	private val algorithm = Algorithm
		.HMAC512(secret)

	val verifier: JWTVerifier = JWT
		.require(algorithm)
		.withIssuer(issuer)
		.build()

	fun createToken(
		profile: DesolveUserProfile
	): String
	{
		return JWT.create()
			.withSubject("Authentication")
			.withIssuer(issuer)
			.withClaim("uniqueId", profile.uniqueId.toString())
			.withExpiresAt(expiration())
			.sign(algorithm)
	}

	private fun expiration() = Date(
		System.currentTimeMillis() + validity
	)
}
