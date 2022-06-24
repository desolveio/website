package io.desolve.website

import dev.forst.ktor.ratelimiting.RateLimiting
import io.desolve.services.artifacts.dao.DesolveStoredArtifactService
import io.desolve.services.artifacts.dao.DesolveStoredProjectService
import io.desolve.services.distcache.DesolveDistcacheService
import io.desolve.services.profiles.DesolveUserProfilePlatformTools
import io.desolve.website.authentication.JwtConfig
import io.desolve.website.routing.router
import io.desolve.website.services.ClientService
import io.desolve.website.services.artifacts.DesolveArtifactContainer
import io.desolve.website.utils.desolveJson
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.locations.Locations
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmInfoMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.net.URL
import java.time.Duration
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

val service = DesolveStoredArtifactService()
val projectService = DesolveStoredProjectService()

fun main()
{
    // suppress mongodb logging like I want to suppress the headaches growly gives me
    Logger.getLogger("org.mongodb.driver").level = Level.SEVERE

    embeddedServer(
        Netty,
        host = "0.0.0.0",
        port = 8080,
        watchPaths = listOf("classes", "resources")
    ) {
        DesolveDistcacheService
            .container(
                DesolveArtifactContainer()
            )

        routing()
        println("Started website on http://localhost:8080")

        Runtime.getRuntime().addShutdownHook(Thread {
            println("Shutting down client services...")

            ClientService.close()
        })
    }.start(wait = true)
}

fun Application.routing()
{
    configureRouting()
    configureAuthentication()

    routing {
        configureSPA()
    }
}

val profileService by lazy {
    DesolveUserProfilePlatformTools.service()
}

fun Application.configureAuthentication()
{
    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            this.realm = "desolve.io"

            validate {
                val uniqueId = it.payload
                    .getClaim("uniqueId")
                    .asString()
                    ?: return@validate null

                return@validate profileService
                    .findByUniqueId(
                        UUID.fromString(uniqueId)
                    )
            }
        }
    }
}

private fun Application.configureRouting()
{
    // TODO: improve?
    val excluded = listOf(
        "/api/artifacts/basicLookup",
        "/api/artifacts/projectData",
        "/api/artifacts/metrics",
        "/api/auth/optional",
        "/api/auth/register/verify",
        "/api/setup/setupData",
        "/api/user/information",
        "/api/user/information/view"
    )

    val registry =
        PrometheusMeterRegistry(
            PrometheusConfig.DEFAULT
        )

    install(Locations)
    install(RateLimiting) {
        registerLimit(
            limit = 10,
            window = Duration
                .ofSeconds(25)
        ) {
            this.request.origin.host
        }

        rateLimitHit { _, retryAfter ->
            this.request.call.respond(
                HttpStatusCode.TooManyRequests,
                mapOf(
                    "description" to "Rate Limit Hit",
                    "retryAfter" to "$retryAfter",
                    "retryAfterTimeUnit" to "SECONDS"
                )
            )
        }

        excludeRequestWhen {
            !request.path().startsWith("/api") ||
                    excluded.any {
                        this.request.path()
                            .lowercase()
                            .startsWith(it)
                    }
        }
    }
    install(ContentNegotiation) {
        json(
            json = desolveJson
        )
    }

    val metrics = System
        .getProperty("io.desolve.website.status")
        ?.toBoolean() ?: false

    if (metrics)
    {
        install(MicrometerMetrics) {
            this.registry = registry
            this.meterBinders = listOf(
                ClassLoaderMetrics(),
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                ProcessorMetrics(),
                JvmThreadMetrics(),
                JvmInfoMetrics(),
                FileDescriptorMetrics(),
                UptimeMetrics()
            )
        }

        routing {
            get("metrics") {
                // TODO: "firewall" - call.request.local.remoteHost
                call.respondText(registry.scrape())
            }
        }
    }

    this.router(registry)

    val betterStatusCodes = System
        .getProperty("io.desolve.website.status")
        ?.toBoolean() ?: false

    install(StatusPages) {
        if (betterStatusCodes)
        {
            val cat404 = URL("https://http.cat/404")
                .readBytes()

            status(HttpStatusCode.NotFound) { call, _ ->
                call.respondBytes(
                    status = HttpStatusCode.NotFound,
                    bytes = cat404
                )
            }

            return@install
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                mapOf(
                    "description" to "404 Not Found"
                )
            )
        }

        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(
                mapOf(
                    "description" to "401 Unauthorized"
                )
            )
        }
    }
}

val development = System
    .getProperty("io.ktor.development")
    ?.toBoolean() ?: false

private fun Routing.configureSPA()
{
    // if we are in development, we don't want to serve SPA
    // with SPA enabled, we can't see 404s from the API with ease
    // as it redirects to the SPA

    if (development)
    {
        println("Skipping SPA configuration as a development environment has been detected.")
        return
    }

    // configure KTor to serve React content
    singlePageApplication {
        react("static")
        useResources = true
    }
}
