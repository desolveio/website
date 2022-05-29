package io.desolve.website.services

import io.desolve.services.core.client.DesolveClientConstants
import io.desolve.services.core.client.DesolveClientService
import io.desolve.services.protocol.StowageGrpcKt
import io.desolve.services.protocol.WorkerGrpcKt

/**
 * @author GrowlyX
 * @since 5/29/2022
 */
object ClientService
{
    private val artifactChannel = DesolveClientConstants
        .build(DesolveClientConstants.ARTIFACT_RESOLVER)

    private val workerChannel = DesolveClientConstants
        .build(DesolveClientConstants.WORKER_RESOLVER)

    val artifactClient =
        DesolveClientService(
            this.artifactChannel,
            StowageGrpcKt
                .StowageCoroutineStub(
                    this.artifactChannel
                )
        )

    val workerClient =
        DesolveClientService(
            this.workerChannel,
            WorkerGrpcKt
                .WorkerCoroutineStub(
                    this.workerChannel
                )
        )

    fun close()
    {
        this.artifactChannel.shutdownNow()
        this.workerChannel.shutdownNow()
    }
}
