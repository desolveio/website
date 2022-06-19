package io.desolve.website.services.artifacts

import io.desolve.services.distcache.DesolveDistcacheContainer
import io.desolve.services.profiles.DesolveUserProfile
import io.desolve.website.utils.desolveJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.time.Duration
import java.util.UUID

/**
 * Useful methods to find a location of an
 * artifact referred to by its artifactId.
 *
 * @author GrowlyX
 * @since 5/23/2022
 */
class DesolveArtifactContainer : DesolveDistcacheContainer()
{
    private val artifactLocations =
        "desolve:artifacts:locations"

    private val verificationLocations =
        "desolve:profile:verification"

    fun findArtifactLocation(
        artifactId: String
    ): String?
    {
        return this.connection().sync()
            .hget(
                this.artifactLocations, artifactId
            )
    }

    fun publishExpiringVerificationCode(
        email: String, code: UUID, adIf: DesolveUserProfile
    )
    {
        this.connection().sync()
            .set(
                "$verificationLocations:$code",
                email
            )

        this.connection().sync()
            .set(
                "$verificationLocations:$code-adif",
                desolveJson.encodeToString(adIf)
            )

        this.connection().sync()
            .expire(
                "$verificationLocations:$code",
                Duration.ofMinutes(5L)
            )

        this.connection().sync()
            .expire(
                "$verificationLocations:$code-adif",
                Duration.ofMinutes(6L)
            )
    }

    fun invalidateCode(code: UUID)
    {
        this.connection().sync()
            .del(
                "$verificationLocations:$code"
            )

        this.connection().sync()
            .del(
                "$verificationLocations:$code-adif"
            )
    }

    fun isValidCode(code: UUID) =
        this.connection().sync()
            .get("$verificationLocations:$code") != null

    fun getInformation(code: UUID) =
        desolveJson
            .decodeFromString<DesolveUserProfile>(
                this.connection().sync().get(
                    "$verificationLocations:$code-adif"
                )
            )
}
