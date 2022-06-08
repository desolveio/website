package io.desolve.website.services

import io.desolve.services.core.client.DesolveClientConstants
import io.desolve.services.core.client.DesolveClientService
import io.desolve.services.core.client.resolver.MultiAddressNameResolverFactory
import io.desolve.services.protocol.StowageGrpcKt
import io.desolve.services.protocol.WorkerGrpcKt

/**
 * @author GrowlyX
 * @since 5/29/2022
 */
object ClientService
{
	// TODO: 6/8/2022 change when in docker prod
	private val artifactChannel = DesolveClientConstants
		// DesolveClientConstants.ARTIFACT_RESOLVER
		.build {
			MultiAddressNameResolverFactory(
				"localhost" to 50550
			)
		}

	private val workerChannel = DesolveClientConstants
		// DesolveClientConstants.ARTIFACT_RESOLVER
		.build {
			MultiAddressNameResolverFactory(
				"localhost" to 50500
			)
		}

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
