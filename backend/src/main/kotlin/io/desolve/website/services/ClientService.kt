package io.desolve.website.services

import io.desolve.services.core.DesolveServiceMeta
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
		.build(DesolveServiceMeta.Artifacts)

	private val workerChannel = DesolveClientConstants
		.build(DesolveServiceMeta.Workers)

	val artifactClient =
		DesolveClientService(
			artifactChannel,
			StowageGrpcKt
				.StowageCoroutineStub(
					artifactChannel
				)
		)

	val workerClient =
		DesolveClientService(
			workerChannel,
			WorkerGrpcKt
				.WorkerCoroutineStub(
					workerChannel
				)
		)

	fun close()
	{
		artifactChannel.shutdownNow()
		workerChannel.shutdownNow()
	}
}
