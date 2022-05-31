package io.desolve.website.authentication.login

import kotlinx.serialization.Serializable

/**
 * @author GrowlyX
 * @since 5/29/2022
 */
@Serializable
data class LoginRequest(val email: String, val password: String)
