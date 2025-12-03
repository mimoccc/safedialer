package org.mjdev.safedialer.providers.android.calendar

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract.Attendees
import android.provider.CalendarContract.Calendars
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Instances
import android.provider.CalendarContract.Reminders
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class CalendarProvider(
    context: Context
) : AbstractProvider(context) {
    fun getCalendars(): Data<Calendar>? {
        return getContentTableData(Calendar.uri, Calendar::class.java)
    }

    fun getCalendar(calendarId: Long): Calendar? {
        val selection = "(${Calendars._ID} = ?)"
        val selectionArgs = arrayOf(calendarId.toString())
        return getContentRowData(Calendar.uri, selection, selectionArgs, null, Calendar::class.java)
    }

    fun getEvents(calendarId: Long): Data<Event>? {
        val selection = "(${Events.CALENDAR_ID} = ?)"
        val selectionArgs = arrayOf(calendarId.toString())
        return getContentTableData(Event.uri, selection, selectionArgs, null, Event::class.java)
    }

    fun getEvent(eventId: Long): Event? {
        val selection = "(${Events._ID} = ?)"
        val selectionArgs = arrayOf(eventId.toString())
        return getContentRowData(Event.uri, selection, selectionArgs, null, Event::class.java)
    }

    fun getInstances(begin: Long, end: Long): Data<Instance>? {
        val builder = Instance.uri.buildUpon()
        ContentUris.appendId(builder, begin)
        ContentUris.appendId(builder, end)
        val uri = builder.build()
        return getContentTableData(uri, Instance::class.java)
    }

    fun getInstances(eventId: Long, begin: Long, end: Long): Data<Instance>? {
        val selection = "(${Instances.EVENT_ID} = ?)"
        val selectionArgs = arrayOf(eventId.toString())
        val builder = Instance.uri.buildUpon()
        ContentUris.appendId(builder, begin)
        ContentUris.appendId(builder, end)
        val uri = builder.build()
        return getContentTableData(uri, selection, selectionArgs, null, Instance::class.java)
    }

    fun getAttendees(eventId: Long): Data<Attendee>? {
        val selection = "(${Attendees.EVENT_ID}=?)"
        val selectionArgs = arrayOf(eventId.toString())
        return getContentTableData(Attendee.uri, selection, selectionArgs, null, Attendee::class.java)
    }

    fun getReminders(eventId: Long): Data<Reminder>? {
        val selection = "(${Reminders.EVENT_ID}=?)"
        val selectionArgs = arrayOf(eventId.toString())
        return getContentTableData(Reminder.uri, selection, selectionArgs, null, Reminder::class.java)
    }

    fun update(calendar: Calendar): Int = updateTableRow(Calendar.uri, calendar)

    fun update(event: Event): Int = updateTableRow(Event.uri, event)

    fun update(instance: Instance): Int = updateTableRow(Instance.uri, instance)

    fun update(reminder: Reminder): Int = updateTableRow(Reminder.uri, reminder)

    fun update(attendee: Attendee): Int = updateTableRow(Attendee.uri, attendee)

    override fun getUris(): List<Uri> = listOf(
        Attendee.uri,
        Reminder.uri,
        Instance.uri,
        Event.uri,
        Calendar.uri
    ).distinct().filter { it != Uri.EMPTY }
}
