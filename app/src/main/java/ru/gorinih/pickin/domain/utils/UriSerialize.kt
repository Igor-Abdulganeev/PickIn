package ru.gorinih.pickin.domain.utils

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import androidx.core.net.toUri

/**
 * Created by Igor Abdulganeev on 23.11.2026
 */

object UriSerialize: KSerializer<Uri> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName = "DataUri", PrimitiveKind.STRING)


    override fun serialize(
        encoder: Encoder,
        value: Uri
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Uri = decoder.decodeString().toUri()
}