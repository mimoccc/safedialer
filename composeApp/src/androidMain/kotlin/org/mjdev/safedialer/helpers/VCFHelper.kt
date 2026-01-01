package org.mjdev.safedialer.helpers

import android.content.ContentUris.withAppendedId
import android.content.Context
import android.net.Uri.withAppendedPath
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_WORK
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Relation
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import android.provider.ContactsContract.Contacts
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.parameter.ImageType
import ezvcard.parameter.RelatedType
import ezvcard.property.Address
import ezvcard.property.Anniversary
import ezvcard.property.Birthday
import ezvcard.property.Categories
import ezvcard.property.Email
import ezvcard.property.FormattedName
import ezvcard.property.Organization
import ezvcard.property.Photo
import ezvcard.property.Related
import ezvcard.property.SortString
import ezvcard.property.StructuredName
import ezvcard.property.Telephone
import ezvcard.property.Title
import ezvcard.property.Uid
import ezvcard.property.Url
import org.mjdev.safedialer.providers.android.contacts.Contact
import org.mjdev.safedialer.providers.core.LabeledValue
import java.nio.file.Path

@Suppress("unused")
object VCFHelper {

    private fun getContactPhotoBytes(
        context: Context,
        contactId: String
    ): ByteArray? {
        val contactUri = withAppendedId(Contacts.CONTENT_URI, contactId.toLong())
        val photoUri = withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY)
        val cursor = context.contentResolver.query(
            photoUri,
            arrayOf(Contacts.Photo.PHOTO),
            null,
            null,
            null
        ) ?: return null
        cursor.use { c ->
            if (c.moveToFirst()) {
                return c.getBlob(0)
            }
        }
        return null
    }

    fun toVcfData(
        contact: Contact,
        context: Context
    ): ByteArray = VCard().apply {
        uid = Uid(contact.id.toString())
        structuredName = StructuredName().apply {
            family = contact.lastName
            given = contact.firstName
            contact.middleName?.let { additionalNames.add(it) }
            contact.namePrefix?.let { prefixes.add(it) }
            contact.nameSuffix?.let { suffixes.add(it) }
            contact.phoneticName?.let { sortString = SortString(it) }
        }
        formattedName = FormattedName(contact.displayName)
        organization = Organization().apply {
            contact.company?.let { values.add(it) }
            contact.department?.let { values.add(it) }
        }
        contact.jobTitle?.let { titles.add(Title(it)) }
        contact.phones?.forEach { p ->
            addTelephoneNumber(Telephone(p.value))
        }
        contact.emails?.forEach { e ->
            addEmail(Email(e.value))
        }
        contact.addresses?.forEach { a ->
            addAddress(Address().apply {
                streetAddress = a.value
            })
        }
        contact.importantDates?.forEach { d ->
            when (d.type) {
                Event.TYPE_BIRTHDAY -> birthdays.add(Birthday(d.value))
                Event.TYPE_ANNIVERSARY -> anniversaries.add(Anniversary(d.value))
                else -> addNote("Other date (${d.label}): ${d.value}")
            }
        }
        contact.relationships?.forEach { r ->
            addRelated(Related().apply {
                text = r.value
                r.label?.let { types.add(RelatedType.get(it)) }
                when (r.type) {
                    Relation.TYPE_SPOUSE -> types.add(RelatedType.SPOUSE)
                    Relation.TYPE_FATHER -> types.add(RelatedType.get("father"))
                    Relation.TYPE_MOTHER -> types.add(RelatedType.get("mother"))
                    Relation.TYPE_PARENT -> types.add(RelatedType.PARENT)
                    Relation.TYPE_BROTHER -> types.add(RelatedType.get("brother"))
                    Relation.TYPE_SISTER -> types.add(RelatedType.get("sister"))
                    Relation.TYPE_CHILD -> types.add(RelatedType.CHILD)
                    Relation.TYPE_FRIEND -> types.add(RelatedType.FRIEND)
                    Relation.TYPE_PARTNER -> types.add(RelatedType.get("partner"))
                    Relation.TYPE_MANAGER -> types.add(RelatedType.get("manager"))
                    Relation.TYPE_ASSISTANT -> types.add(RelatedType.get("assistant"))
                    Relation.TYPE_REFERRED_BY -> types.add(RelatedType.get("referred-by"))
                }
            })
        }
        contact.notes?.let { n ->
            addNote(n)
        }
        contact.websites?.forEach { u ->
            addUrl(Url(u))
        }
        contact.groups?.let { g ->
            if (g.isNotEmpty()) {
                val categories = Categories()
                g.forEach { categories.values.add(it) }
                addCategories(categories)
            }
        }
        val pBytes = contact.photoBytes ?:
        getContactPhotoBytes(context, contact.contactId.toString())
        pBytes?.let { b ->
            addPhoto(Photo(b, ImageType.JPEG))
        }
    }.let { vcard ->
        Ezvcard.write(vcard).go()
    }.toByteArray()

    fun parseContact(
        filePath: Path,
        data: ByteArray?
    ): Contact {
        val vcard = Ezvcard.parse(data?.inputStream()).first()
        return Contact().apply {
            id = vcard.uid.value.toLong()
            displayName = vcard.formattedName?.value
            firstName = vcard.structuredName?.given
            lastName = vcard.structuredName?.family
            middleName = vcard.structuredName?.additionalNames?.firstOrNull()
            namePrefix = vcard.structuredName?.prefixes?.firstOrNull()
            nameSuffix = vcard.structuredName?.suffixes?.firstOrNull()
            phones = vcard.telephoneNumbers.map { tel ->
                val type = when {
                    tel.types.contains(ezvcard.parameter.TelephoneType.HOME) -> Phone.TYPE_HOME
                    tel.types.contains(ezvcard.parameter.TelephoneType.WORK) -> Phone.TYPE_WORK
                    tel.types.contains(ezvcard.parameter.TelephoneType.CELL) -> Phone.TYPE_MOBILE
                    tel.types.contains(ezvcard.parameter.TelephoneType.FAX) -> Phone.TYPE_FAX_WORK
                    tel.types.contains(ezvcard.parameter.TelephoneType.PAGER) -> Phone.TYPE_PAGER
                    else -> Phone.TYPE_OTHER
                }
                LabeledValue(tel.text, type)
            }
            emails = vcard.emails.map { email ->
                val type = when {
                    email.types.contains(ezvcard.parameter.EmailType.HOME) -> android.provider.ContactsContract.CommonDataKinds.Email.TYPE_HOME
                    email.types.contains(ezvcard.parameter.EmailType.WORK) -> android.provider.ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    else -> android.provider.ContactsContract.CommonDataKinds.Email.TYPE_OTHER
                }
                LabeledValue(email.value, type)
            }
            notes = vcard.notes.firstOrNull()?.value
            company = vcard.organization?.values?.firstOrNull()
            jobTitle = vcard.titles.firstOrNull()?.value
            phoneticName = vcard.sortString?.value
            addresses = vcard.addresses.map { addr ->
                val type = when {
                    addr.types.contains(ezvcard.parameter.AddressType.HOME) -> StructuredPostal.TYPE_HOME
                    addr.types.contains(ezvcard.parameter.AddressType.WORK) -> StructuredPostal.TYPE_WORK
                    else -> StructuredPostal.TYPE_OTHER
                }
                LabeledValue(addr.streetAddress ?: "", type)
            }
            websites = vcard.urls.map { it.value }
            importantDates = mutableListOf<LabeledValue>().apply {
                vcard.birthdays.firstOrNull()?.let {
                    add(LabeledValue(it.text ?: "", Event.TYPE_BIRTHDAY))
                }
                vcard.anniversaries.firstOrNull()?.let {
                    add(LabeledValue(it.text ?: "", Event.TYPE_ANNIVERSARY))
                }
            }
            relationships = vcard.relations.map { rel ->
                val type = when {
                    rel.types.contains(RelatedType.SPOUSE) -> Relation.TYPE_SPOUSE
                    rel.types.contains(RelatedType.PARENT) -> Relation.TYPE_PARENT
                    rel.types.contains(RelatedType.CHILD) -> Relation.TYPE_CHILD
                    rel.types.contains(RelatedType.FRIEND) -> Relation.TYPE_FRIEND
                    else -> Relation.TYPE_FRIEND
                }
                LabeledValue(rel.text ?: "", type)
            }
            photoBytes = vcard.photos.firstOrNull()?.data
        }
    }
}