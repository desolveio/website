package io.desolve.website.utils

import com.github.jershell.kbson.BigDecimalSerializer
import com.github.jershell.kbson.ByteArraySerializer
import com.github.jershell.kbson.DateSerializer
import com.github.jershell.kbson.KBson
import com.github.jershell.kbson.ObjectIdSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.bson.BsonTimestamp
import org.bson.types.Binary
import org.bson.types.ObjectId
import org.litote.kmongo.serialization.BinarySerializer
import org.litote.kmongo.serialization.BsonTimestampSerializer
import org.litote.kmongo.serialization.CalendarSerializer
import org.litote.kmongo.serialization.InstantSerializer
import org.litote.kmongo.serialization.LocalDateSerializer
import org.litote.kmongo.serialization.LocalDateTimeSerializer
import org.litote.kmongo.serialization.LocalTimeSerializer
import org.litote.kmongo.serialization.LocaleSerializer
import org.litote.kmongo.serialization.OffsetDateTimeSerializer
import org.litote.kmongo.serialization.OffsetTimeSerializer
import org.litote.kmongo.serialization.ZonedDateTimeSerializer
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

val kbson = KBson(serializersModule = SerializersModule {
	contextual(ObjectId::class, ObjectIdSerializer)
	contextual(BigDecimal::class, BigDecimalSerializer)
	contextual(ByteArray::class, ByteArraySerializer)
	contextual(Date::class, DateSerializer)
	contextual(UUID::class, DesolveUUIDSerializer)
	// KMongo
	contextual(Calendar::class, CalendarSerializer)
	//contextual(GregorianCalendar::class, CalendarSerializer)
	contextual(Instant::class, InstantSerializer)
	contextual(ZonedDateTime::class, ZonedDateTimeSerializer)
	contextual(OffsetDateTime::class, OffsetDateTimeSerializer)
	contextual(LocalDate::class, LocalDateSerializer)
	contextual(LocalDateTime::class, LocalDateTimeSerializer)
	contextual(LocalTime::class, LocalTimeSerializer)
	contextual(OffsetTime::class, OffsetTimeSerializer)
	contextual(BsonTimestamp::class, BsonTimestampSerializer)
	contextual(Locale::class, LocaleSerializer)
	contextual(Binary::class, BinarySerializer)
})

inline fun <reified T> T.stringify(): String
{
	// this is very hacky but essentially KMongo-serialization uses KBson to convert, so we will use it as well :-)
	/*return kbson.stringify(serializer(), this)
		.toJson()*/

	return desolveJson.encodeToString(this)
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