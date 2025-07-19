package org.mjdev.safedialer.webdav.webdavlib

import org.mjdev.safedialer.webdav.property.push.PushMessage
import org.mjdev.safedialer.webdav.property.push.PushRegister
import org.mjdev.safedialer.webdav.property.push.PushTransports
import org.mjdev.safedialer.webdav.property.push.Subscription
import org.mjdev.safedialer.webdav.property.push.Topic
import org.mjdev.safedialer.webdav.property.push.WebPushSubscription
import org.mjdev.safedialer.webdav.property.webdav.AddMember
import org.mjdev.safedialer.webdav.property.webdav.CreationDate
import org.mjdev.safedialer.webdav.property.webdav.CurrentUserPrincipal
import org.mjdev.safedialer.webdav.property.webdav.CurrentUserPrivilegeSet
import org.mjdev.safedialer.webdav.property.webdav.DisplayName
import org.mjdev.safedialer.webdav.property.webdav.GetContentLength
import org.mjdev.safedialer.webdav.property.webdav.GetContentType
import org.mjdev.safedialer.webdav.property.webdav.GetETag
import org.mjdev.safedialer.webdav.property.webdav.GetLastModified
import org.mjdev.safedialer.webdav.property.webdav.GroupMembership
import org.mjdev.safedialer.webdav.property.webdav.Owner
import org.mjdev.safedialer.webdav.property.webdav.QuotaAvailableBytes
import org.mjdev.safedialer.webdav.property.webdav.QuotaUsedBytes
import org.mjdev.safedialer.webdav.property.webdav.ResourceType
import org.mjdev.safedialer.webdav.property.webdav.SupportedReportSet
import org.mjdev.safedialer.webdav.property.webdav.SyncToken
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.util.logging.Level
import java.util.logging.Logger

object PropertyRegistry {
    private val factories = mutableMapOf<Property.Name, PropertyFactory>()
    private val logger
        get() = Logger.getLogger(javaClass.name)

    init {
        logger.info("Registering DAV property factories")
        registerDefaultFactories()
    }

    private fun registerDefaultFactories() {
        register(listOf(
            AddMember.Factory,
//            AddressbookDescription.Factory,
//            AddressbookHomeSet.Factory,
//            AddressData.Factory,
//            CalendarColor.Factory,
//            CalendarData.Factory,
//            CalendarDescription.Factory,
//            CalendarHomeSet.Factory,
//            CalendarProxyReadFor.Factory,
//            CalendarProxyWriteFor.Factory,
//            CalendarTimezone.Factory,
//            CalendarTimezoneId.Factory,
//            CalendarUserAddressSet.Factory,
            CreationDate.Factory,
            CurrentUserPrincipal.Factory,
            CurrentUserPrivilegeSet.Factory,
            DisplayName.Factory,
            GetContentLength.Factory,
            GetContentType.Factory,
//            GetCTag.Factory,
            GetETag.Factory,
            GetLastModified.Factory,
            GroupMembership.Factory,
//            at.bitfire.dav4jvm.okhttp.property.caldav.MaxResourceSize.Factory,
//            at.bitfire.dav4jvm.okhttp.property.carddav.MaxResourceSize.Factory,
            Owner.Factory,
            PushMessage.Factory,
            PushRegister.Factory,
            PushTransports.Factory,
            QuotaAvailableBytes.Factory,
            QuotaUsedBytes.Factory,
            ResourceType.Factory,
//            ScheduleTag.Factory,
//            Source.Factory,
            Subscription.Factory,
//            SupportedAddressData.Factory,
//            SupportedCalendarComponentSet.Factory,
//            SupportedCalendarData.Factory,
            SupportedReportSet.Factory,
            SyncToken.Factory,
            Topic.Factory,
            WebPushSubscription.Factory
        ))
    }

    fun register(factory: PropertyFactory) {
        logger.fine("Registering ${factory::class.java.name} for ${factory.getName()}")
        factories[factory.getName()] = factory
    }

    fun register(factories: Iterable<PropertyFactory>) {
        factories.forEach {
            register(it)
        }
    }

    fun create(name: Property.Name, parser: XmlPullParser) =
        try {
            factories[name]?.create(parser)
        } catch (e: XmlPullParserException) {
            logger.log(Level.WARNING, "Couldn't parse $name", e)
            null
        }
}
