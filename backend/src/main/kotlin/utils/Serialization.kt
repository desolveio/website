package io.desolve.website.utils

import com.github.jershell.kbson.BigDecimalSerializer
import com.github.jershell.kbson.ByteArraySerializer
import com.github.jershell.kbson.Configuration
import com.github.jershell.kbson.DateSerializer
import com.github.jershell.kbson.KBson
import com.github.jershell.kbson.ObjectIdSerializer
import com.github.jershell.kbson.UUIDSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
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

private val Serializers = SerializersModule {
	contextual(ObjectId::class, ObjectIdSerializer)
	contextual(BigDecimal::class, BigDecimalSerializer)
	contextual(ByteArray::class, ByteArraySerializer)
	contextual(Date::class, DateSerializer)
	contextual(UUID::class, UUIDSerializer)
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
}

val kbson = KBson(serializersModule = Serializers, configuration = Configuration(encodeDefaults = false))

inline fun <reified T> T.stringify(): String
{
	// this is very hacky but essentially KMongo-serialization uses KBson to convert, so we will use it as well :-)
	return kbson.stringify(serializer(), this)
		.toJson()
}
