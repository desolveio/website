package io.desolve.website.extensions

import io.desolve.services.profiles.DesolveUserProfile
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authentication

fun ApplicationCall.userProfile() =
	this.authentication.principal<DesolveUserProfile>()

fun ApplicationCall.ensureUserProfile() =
	userProfile()!!