package com.example.network.model.response

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant

@Serializable
data class PostResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val text: String,
    val categoryId: Long,
    @Serializable(with=InstantSerializer::class)
    val timeAtCreation: Instant,
    val likesCount: Long,
    val commentsCount: Long
)

@Serializer(forClass = Instant::class)
@OptIn(ExperimentalSerializationApi::class)
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilli())
    override fun deserialize(decoder: Decoder) = Instant.ofEpochMilli(decoder.decodeLong())
}
