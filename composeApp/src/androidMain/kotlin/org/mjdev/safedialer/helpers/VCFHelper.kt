package org.mjdev.safedialer.helpers

import android.content.ContentUris.withAppendedId
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri.withAppendedPath
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.Relation
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal
import android.provider.ContactsContract.Contacts
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isSpecified
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.parameter.AddressType
import ezvcard.parameter.EmailType
import ezvcard.parameter.ImageType
import ezvcard.parameter.RelatedType
import ezvcard.parameter.TelephoneType
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
    private const val X_PHOTO_URL = "X-PHOTO-URL"

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
        }
        contact.phoneticName?.let { sortString = SortString(it) }
        formattedName = FormattedName(contact.displayName)
        organization = Organization().apply {
            contact.company?.let { values.add(it) }
            contact.department?.let { values.add(it) }
        }
        contact.jobTitle?.let { titles.add(Title(it)) }
        contact.phones?.forEach { p ->
            addTelephoneNumber(Telephone(p.value).apply {
                types.add(
                    when (p.type) {
                        Phone.TYPE_HOME -> TelephoneType.HOME
                        Phone.TYPE_MOBILE -> TelephoneType.CELL
                        Phone.TYPE_WORK -> TelephoneType.WORK
                        Phone.TYPE_FAX_WORK, Phone.TYPE_FAX_HOME -> TelephoneType.FAX
                        Phone.TYPE_PAGER -> TelephoneType.PAGER
                        else -> TelephoneType.HOME
                    }
                )
            })
        }
        contact.emails?.forEach { e ->
            addEmail(Email(e.value).apply {
                types.add(
                    when (e.type) {
                        ContactsContract.CommonDataKinds.Email.TYPE_HOME -> EmailType.HOME
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK -> EmailType.WORK
                        else -> EmailType.HOME
                    }
                )
            })
        }
        contact.addresses?.forEach { a ->
            addAddress(Address().apply {
                val parts = a.value.split("|")
                streetAddress = parts.getOrNull(0)
                locality = parts.getOrNull(1)
                region = parts.getOrNull(2)
                postalCode = parts.getOrNull(3)
                country = parts.getOrNull(4)
                types.add(
                    when (a.type) {
                        StructuredPostal.TYPE_HOME -> AddressType.HOME
                        StructuredPostal.TYPE_WORK -> AddressType.WORK
                        else -> AddressType.HOME
                    }
                )
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
        contact.photoUrls?.forEach { photoUrl ->
            addExtendedProperty(X_PHOTO_URL, photoUrl)
        }
        contact.let { c ->
            val pBytes = c.photoBytes ?: getContactPhotoBytes(
                context,
                contact.contactId.toString()
            )
            pBytes?.let { b ->
                addPhoto(Photo(b, ImageType.JPEG))
            }
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
            id = vcard.uid?.value?.toLong() ?: 0L
            displayName = vcard.formattedName?.value
            firstName = vcard.structuredName?.given
            lastName = vcard.structuredName?.family
            middleName = vcard.structuredName?.additionalNames?.firstOrNull()
            namePrefix = vcard.structuredName?.prefixes?.firstOrNull()
            nameSuffix = vcard.structuredName?.suffixes?.firstOrNull()
            phoneticName = vcard.sortString?.value
            company = vcard.organization?.values?.firstOrNull()
            department = vcard.organization?.values?.getOrNull(1)
            jobTitle = vcard.titles.firstOrNull()?.value
            phones = vcard.telephoneNumbers.map { tel ->
                val type = when {
                    tel.types.contains(TelephoneType.HOME) -> Phone.TYPE_HOME
                    tel.types.contains(TelephoneType.WORK) -> Phone.TYPE_WORK
                    tel.types.contains(TelephoneType.CELL) -> Phone.TYPE_MOBILE
                    tel.types.contains(TelephoneType.FAX) -> Phone.TYPE_FAX_WORK
                    tel.types.contains(TelephoneType.PAGER) -> Phone.TYPE_PAGER
                    else -> Phone.TYPE_OTHER
                }
                LabeledValue(tel.text, type)
            }
            emails = vcard.emails.map { email ->
                val type = when {
                    email.types.contains(EmailType.HOME) ->
                        ContactsContract.CommonDataKinds.Email.TYPE_HOME
                    email.types.contains(EmailType.WORK) ->
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    else -> ContactsContract.CommonDataKinds.Email.TYPE_OTHER
                }
                LabeledValue(email.value, type)
            }
            addresses = vcard.addresses.map { addr ->
                val type = when {
                    addr.types.contains(AddressType.HOME) -> StructuredPostal.TYPE_HOME
                    addr.types.contains(AddressType.WORK) -> StructuredPostal.TYPE_WORK
                    else -> StructuredPostal.TYPE_OTHER
                }
                val fullAddress = listOfNotNull(
                    addr.streetAddress,
                    addr.locality,
                    addr.region,
                    addr.postalCode,
                    addr.country
                ).joinToString("|")
                LabeledValue(fullAddress, type)
            }
            notes = vcard.notes.firstOrNull()?.value
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
            groups = vcard.categories?.values?.toList() ?: emptyList()
            photoUrls = vcard.getExtendedProperties(X_PHOTO_URL).map { it.value }
            photoBytes = vcard.photos.firstOrNull()?.data
        }
    }

    fun Photo.toImageBitmap(
        width: Dp = Dp.Unspecified,
        height: Dp = Dp.Unspecified
    ): ImageBitmap? {
        return data?.let { photoData ->
            val bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.size)
            if (bitmap != null && (width.isSpecified || height.isSpecified)) {
                val w = if (width.isSpecified) width.value.toInt() else bitmap.width
                val h = if (height.isSpecified) height.value.toInt() else bitmap.height
                Bitmap.createScaledBitmap(bitmap, w, h, true).asImageBitmap()
            } else {
                bitmap?.asImageBitmap()
            }
        }
    }
}
