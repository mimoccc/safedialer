package org.mjdev.safedialer.helpers

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.google.gson.Gson

@Suppress("DEPRECATION", "unused", "MemberVisibilityCanBePrivate")
class PreferencesManager(
    private val context: Context
) {
    private var mGson: Gson = Gson()
    private var mMode: Int = INVALID_VALUE
    private var mName: String? = null
    private var mSharedPreferences: SharedPreferences? = null

    fun setName(
        name: String
    ): PreferencesManager {
        mName = name
        return this
    }

    fun setMode(
        mode: Int
    ): PreferencesManager {
        mMode = mode
        return this
    }

    fun init(): PreferencesManager {
        if (mName?.isEmpty() != false) {
            mName = context.packageName
        }
        if (
            mMode == INVALID_VALUE ||
            (mMode != Context.MODE_PRIVATE &&
                    mMode != Context.MODE_WORLD_READABLE &&
                    mMode != Context.MODE_WORLD_WRITEABLE)
        ) {
            mMode = Context.MODE_PRIVATE
        }
        mSharedPreferences = context.getSharedPreferences(mName, mMode)
        return this
    }

    fun putString(
        key: String?,
        value: String?
    ): PreferencesManager {
        mSharedPreferences?.edit()?.apply {
            putString(key, value)
            apply()
        }
        return this
    }

    fun getString(
        key: String?,
        defValue: String?
    ): String? = mSharedPreferences?.getString(key, defValue) ?: defValue

    fun getString(
        key: String?
    ): String? = getString(key, "")

    fun putStringSet(
        key: String?,
        values: MutableSet<String?>?
    ): PreferencesManager {
        mSharedPreferences?.edit()?.apply {
            putStringSet(key, values)
            apply()
        }
        return this
    }

    fun getStringSet(
        key: String?,
        defValues: MutableSet<String?>?
    ): MutableSet<String?>? = mSharedPreferences?.getStringSet(key, defValues) ?: defValues

    fun getStringSet(
        key: String?
    ): MutableSet<String?>? = getStringSet(key, HashSet())

    fun putInt(
        key: String?,
        value: Int
    ): PreferencesManager {
        mSharedPreferences?.edit()?.apply {
            putInt(key, value)
            apply()
        }
        return this
    }

    fun getInt(
        key: String?,
        defValue: Int
    ): Int = mSharedPreferences?.getInt(key, defValue) ?: defValue

    fun getInt(
        key: String?
    ): Int = getInt(key, 0)

    fun putFloat(
        key: String?,
        value: Float
    ): PreferencesManager {
        mSharedPreferences?.edit()?.apply {
            putFloat(key, value)
            apply()
        }
        return this
    }

    fun getFloat(
        key: String?,
        defValue: Float
    ): Float = mSharedPreferences?.getFloat(key, defValue) ?: defValue

    fun getFloat(
        key: String?
    ): Float = getFloat(key, 0f)

    fun putLong(
        key: String?,
        value: Long
    ): PreferencesManager {
        mSharedPreferences?.edit()?.apply {
            putLong(key, value)
            apply()
        }
        return this
    }

    fun getLong(
        key: String?,
        defValue: Long
    ): Long = mSharedPreferences?.getLong(key, defValue) ?: defValue

    fun getLong(
        key: String?
    ): Long = getLong(key, 0)

    fun putBoolean(
        key: String?,
        value: Boolean
    ): PreferencesManager {
        mSharedPreferences?.edit()?.apply {
            putBoolean(key, value)
            apply()
        }
        return this
    }

    fun getBoolean(
        key: String?,
        defValue: Boolean
    ): Boolean = mSharedPreferences?.getBoolean(key, defValue) ?: defValue

    fun getBoolean(
        key: String?
    ): Boolean = getBoolean(key, false)

    fun putObject(
        key: String?,
        value: Any?
    ): PreferencesManager {
        if (value == null) remove(key)
        else putString(key, mGson.toJson(value))
        return this
    }

    fun <T> getObject(
        key: String?,
        type: Class<T?>
    ): T? = if (mSharedPreferences == null) null else mGson.fromJson<T?>(getString(key), type)

    fun remove(key: String?): PreferencesManager {
        mSharedPreferences?.edit()?.remove(key)?.apply()
        return this
    }

    fun clear(): PreferencesManager {
        mSharedPreferences?.edit()?.clear()?.apply()
        return this
    }

    fun registerOnChangeListener(listener: OnSharedPreferenceChangeListener?) {
        mSharedPreferences?.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnChangeListener(listener: OnSharedPreferenceChangeListener?) {
        mSharedPreferences?.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        const val INVALID_VALUE = -1
    }
}