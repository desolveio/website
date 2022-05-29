package io.desolve.website.routing

import io.desolve.website.authentication.JwtConfig
import io.desolve.website.authentication.login.LoginRequest
import io.desolve.website.extensions.userProfile
import io.desolve.website.profileService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils

/**
 * @author GrowlyX
 * @since 5/29/2022
 */
fun Application.configureApiRouting()
{
    routing {
        route("api")
        {
            // TODO: 5/28/2022 logic for login & authenticated routing
            //  https://github.com/AndreasVolkmann/ktor-auth-jwt-sample/blob/master/src/main/kotlin/me/avo/io/ktor/auth/jwt/sample/Module.kt
            post("login") {
                val credentials = this.call
                    .receive<LoginRequest>()

                val user = profileService
                    .findByEmail(credentials.email)

                if (user == null)
                {
                    this.call.respondText("invalid email")
                    return@post
                }

                val sha256 = DigestUtils
                    .sha256Hex(
                        credentials.password
                    )

                // TODO: 5/29/2022 SHA-256 pass on registration
                if (user.password != sha256)
                {
                    this.call.respondText("invalid password")
                    return@post
                }

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
}
