package io.desolve.website.services

import io.desolve.services.containers.DesolveContainerHelper
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
	private val artifactChannel = DesolveClientConstants
		.build {
			MultiAddressNameResolverFactory(
				DesolveContainerHelper.addressOrHost() to 50550
			)
		}

	private val workerChannel = DesolveClientConstants
		.build {
			MultiAddressNameResolverFactory(
				DesolveContainerHelper.addressOrHost() to 50500
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
