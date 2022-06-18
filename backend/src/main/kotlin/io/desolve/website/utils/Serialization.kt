package io.desolve.website.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.Instant
import java.util.UUID

val desolveJson = Json {
	serializersModule = SerializersModule {
		contextual(UUID::class, DesolveUUIDSerializer)
		contextual(Instant::class, DesolveInstantSerializer)
	}

	// KTor Defaults
	encodeDefaults = true
	isLenient = true
	allowSpecialFloatingPointValues = true
	allowStructuredMapKeys = true
	prettyPrint = false
	useArrayPolymorphism = false
}

object DesolveUUIDSerializer : KSerializer<UUID>
{

	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DesolveUUIDSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): UUID =
		UUID.fromString(decoder.decodeString())

	override fun serialize(encoder: Encoder, value: UUID)
	{
		encoder.encodeString(value.toString())
	}

}

object DesolveInstantSerializer : KSerializer<Instant>
{

	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DesolveInstantSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): Instant =
		Instant.parse(decoder.decodeString())

	override fun serialize(encoder: Encoder, value: Instant)
	{
		encoder.encodeString(value.toString())
	}
}
