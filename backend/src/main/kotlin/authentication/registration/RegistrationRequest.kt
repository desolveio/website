package io.desolve.website.authentication.registration

import kotlinx.serialization.Serializable

/**
 * @author GrowlyX
 * @since 5/29/2022
 */
@Serializable
data class RegistrationRequest(
    val email: String, val username: String, val password: String
)
