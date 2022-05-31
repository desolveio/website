package io.desolve.website.regex

/**
 * @author GrowlyX
 * @since 5/31/2022
 */
val emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})\$".toRegex()
val usernameRegex = "(\\w{1,16})\\b".toRegex()
