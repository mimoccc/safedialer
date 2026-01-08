package org.mjdev.safedialer.helpers

import biweekly.ICalVersion
import org.mjdev.safedialer.providers.android.calllog.Call

import biweekly.ICalendar
import biweekly.component.VEvent
import biweekly.io.text.ICalReader
import biweekly.io.text.ICalWriter
import biweekly.property.*
import biweekly.util.Duration
import org.mjdev.safedialer.providers.android.calllog.CallType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

object ToolsCalendar {

    fun toICalFile(
        call: Call
    ): ByteArray = ByteArrayOutputStream().use { outputStream ->
        ICalendar().apply {
            productId = ProductId("-//Call Log//Android//EN")
            VEvent().apply {
                uid = Uid("call-${call.id}@calllog")
                dateStart = DateStart(Date(call.callDate), false)
                duration = DurationProperty(
                    Duration.Builder()
                        .seconds(call.duration.toInt())
                        .build()
                )
                summary = Summary(call.name ?: call.number ?: "Unknown")
                description = Description(buildDescription(call))
                call.type?.let {
                    categories.add(Categories(it.name))
                }
                addExperimentalProperty("X-CALL-ID", call.id.toString())
                call.number?.let {
                    addExperimentalProperty("X-CALL-NUMBER", it)
                }
                call.type?.let {
                    addExperimentalProperty("X-CALL-TYPE", it.name)
                }
                addExperimentalProperty("X-CALL-IS-READ", call.isRead.toString())
                val now = Date()
                dateTimeStamp = DateTimeStamp(now)
                created = Created(now)
            }.also { event ->
                addEvent(event)
            }
        }.let { ical ->
            ICalWriter(outputStream, ICalVersion.V2_0).apply {
                write(ical)
                close()
            }
        }
        outputStream.toByteArray()
    }

    fun parseICalFile(
        data: ByteArray
    ): Call {
        val ical = ByteArrayInputStream(data).use { inputStream ->
            ICalReader(inputStream).use { it.readNext() }
        }
        val event = ical.events.firstOrNull()
            ?: throw IllegalArgumentException("No events found in iCal")
        return Call(
            id = event.getExperimentalProperty("X-CALL-ID")?.value?.toLongOrNull() ?: 0L,
            name = event.summary?.value?.takeIf {
                event.getExperimentalProperty("X-CALL-NUMBER") == null
            },
            callDate = event.dateStart?.value?.time ?: 0L,
            duration = event.duration?.value?.toMillis()?.div(1000) ?: 0L,
            isRead = event.getExperimentalProperty("X-CALL-IS-READ")?.value?.toBoolean() ?: false,
            number = event.getExperimentalProperty("X-CALL-NUMBER").value,
            type = event.getExperimentalProperty("X-CALL-TYPE")?.let { typeName ->
                CallType.entries.find { it.name == typeName.name }
            }
        )
    }

    fun buildDescription(
        call: Call
    ) = buildString {
        append("Call Type: ${call.type?.name ?: "Unknown"}\n")
        call.number?.let { append("Number: $it\n") }
        append("Duration: ${formatDurationHuman(call.duration)}\n")
        append("Read: ${if (call.isRead) "Yes" else "No"}")
    }

    fun formatDurationHuman(
        seconds: Long
    ): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (secs > 0 || (hours == 0L && minutes == 0L)) append("${secs}s")
        }.trim()
    }
}
