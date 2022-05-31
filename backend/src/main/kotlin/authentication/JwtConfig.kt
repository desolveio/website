package io.desolve.website.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.desolve.services.profiles.DesolveUserProfile
import java.time.Duration
import java.util.*

/**
 * @author GrowlyX
 * @since 5/28/2022
 */
object JwtConfig
{
    private const val secret = "desolve-test"
    private const val issuer = "desolve.io"

    private val validity = Duration
        .ofSeconds(10L)
        .toMillis()

    private val algorithm = Algorithm
        .HMAC512(this.secret)

    val verifier: JWTVerifier = JWT
        .require(this.algorithm)
        .withIssuer(this.issuer)
        .build()

    fun createToken(
        profile: DesolveUserProfile
    ): String
    {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(this.issuer)
            .withClaim("uniqueId", profile.uniqueId.toString())
            .withExpiresAt(this.expiration())
            .sign(this.algorithm)
    }

    private fun expiration() = Date(
        System.currentTimeMillis() + this.validity
    )
}
