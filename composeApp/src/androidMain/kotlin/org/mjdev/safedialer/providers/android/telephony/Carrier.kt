package org.mjdev.safedialer.providers.android.telephony

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.provider.Telephony.Carriers
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping
import org.mjdev.safedialer.providers.core.safeUri

@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.KITKAT)
data class Carrier(
    @FieldMapping(
        columnName = Carriers.APN,
        physicalType = FieldMapping.PhysicalType.String
    )
    var apn: String? = null,

    @FieldMapping(
        columnName = Carriers.AUTH_TYPE,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var authType: Int = 0,

    @FieldMapping(
        columnName = Carriers.BEARER,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var bearer: Int = 0,

    @FieldMapping(
        columnName = Carriers.CARRIER_ENABLED,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var carrierEnabled: Boolean = false,

    @FieldMapping(
        columnName = Carriers.CURRENT,
        physicalType = FieldMapping.PhysicalType.Int,
        logicalType = FieldMapping.LogicalType.Boolean
    )
    var current: Boolean = false,

    @FieldMapping(
        columnName = Carriers.MCC,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mcc: String? = null,

    @FieldMapping(
        columnName = Carriers.MMSC,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mmscUrl: String? = null,

    @FieldMapping(
        columnName = Carriers.MMSPORT,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mmsPort: String? = null,

    @FieldMapping(
        columnName = Carriers.MMSPROXY,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mmsProxy: String? = null,

    @FieldMapping(
        columnName = Carriers.MNC,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mnc: String? = null,

    @FieldMapping(
        columnName = Carriers.MVNO_MATCH_DATA,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mvnoMatchData: String? = null,

    @FieldMapping(
        columnName = Carriers.MVNO_TYPE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var mvnoType: String? = null,

    @FieldMapping(
        columnName = Carriers.NAME,
        physicalType = FieldMapping.PhysicalType.String
    )
    var name: String? = null,

    @FieldMapping(
        columnName = Carriers.NUMERIC,
        physicalType = FieldMapping.PhysicalType.String
    )
    var numeric: String? = null,

    @FieldMapping(
        columnName = Carriers.PASSWORD,
        physicalType = FieldMapping.PhysicalType.String
    )
    var password: String? = null,

    @FieldMapping(
        columnName = Carriers.PORT,
        physicalType = FieldMapping.PhysicalType.String
    )
    var port: String? = null,

    @FieldMapping(
        columnName = Carriers.PROTOCOL,
        physicalType = FieldMapping.PhysicalType.String
    )
    var protocol: String? = null,

    @FieldMapping(
        columnName = Carriers.ROAMING_PROTOCOL,
        physicalType = FieldMapping.PhysicalType.String
    )
    var roamingProtocol: String? = null,

    @FieldMapping(
        columnName = Carriers.SERVER,
        physicalType = FieldMapping.PhysicalType.String
    )
    var server: String? = null,

    @FieldMapping(
        columnName = Carriers.TYPE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var type: String? = null,

    @FieldMapping(
        columnName = Carriers.USER,
        physicalType = FieldMapping.PhysicalType.String
    )
    var user: String? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = safeUri {
            Carriers.CONTENT_URI
        }
    }
}
