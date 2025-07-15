package org.mjdev.safedialer.helpers

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.google.gson.Gson

@Suppress("DEPRECATION", "unused")
class PreferencesManager(
    private val context: Context
) {
    private var mGson: Gson = Gson()
    private var mMode: Int = INVALID_VALUE
    private var mName: String? = null
    private var mSharedPreferences: SharedPreferences? = null

    fun setName(name: String): PreferencesManager {
        mName = name
        return this
    }

    fun setMode(mode: Int): PreferencesManager {
        mMode = mode
        return this
    }

    fun init(): PreferencesManager {
        if (mName?.isEmpty() ?: true) {
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

    fun putString(key: String?, value: String?) {
        val editor: SharedPreferences.Editor? = mSharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun getString(key: String?, defValue: String?): String? {
        return mSharedPreferences?.getString(key, defValue) ?: defValue
    }

    fun getString(key: String?): String? {
        return getString(key, "")
    }

    fun putStringSet(key: String?, values: MutableSet<String?>?) {
        val editor: SharedPreferences.Editor? = mSharedPreferences?.edit()
        editor?.putStringSet(key, values)
        editor?.apply()
    }

    fun getStringSet(key: String?, defValues: MutableSet<String?>?): MutableSet<String?>? {
        return mSharedPreferences?.getStringSet(key, defValues) ?: defValues
    }

    fun getStringSet(key: String?): MutableSet<String?>? {
        return getStringSet(key, HashSet())
    }

    fun putInt(key: String?, value: Int) {
        val editor: SharedPreferences.Editor? = mSharedPreferences?.edit()
        editor?.putInt(key, value)
        editor?.apply()
    }

    fun getInt(key: String?, defValue: Int): Int {
        return mSharedPreferences?.getInt(key, defValue) ?: defValue
    }

    fun getInt(key: String?): Int {
        return getInt(key, 0)
    }

    fun putFloat(key: String?, value: Float) {
        val editor: SharedPreferences.Editor? = mSharedPreferences?.edit()
        editor?.putFloat(key, value)
        editor?.apply()
    }

    fun getFloat(key: String?, defValue: Float): Float {
        return mSharedPreferences?.getFloat(key, defValue) ?: defValue
    }

    fun getFloat(key: String?): Float {
        return getFloat(key, 0f)
    }

    fun putLong(key: String?, value: Long) {
        val editor: SharedPreferences.Editor? = mSharedPreferences?.edit()
        editor?.putLong(key, value)
        editor?.apply()
    }

    fun getLong(key: String?, defValue: Long): Long {
        return mSharedPreferences?.getLong(key, defValue) ?: defValue
    }

    fun getLong(key: String?): Long {
        return getLong(key, 0)
    }

    fun putBoolean(key: String?, value: Boolean) {
        val editor: SharedPreferences.Editor? = mSharedPreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return mSharedPreferences?.getBoolean(key, defValue) ?: defValue
    }

    fun getBoolean(key: String?): Boolean {
        return getBoolean(key, false)
    }

    fun putObject(key: String?, value: Any?) {
        if (value == null) {
            return
        }
        putString(key, mGson.toJson(value))
    }

    fun <T> getObject(key: String?, type: Class<T?>): T? {
        if (mSharedPreferences == null) {
            return null
        }
        return mGson.fromJson<T?>(getString(key), type)
    }

    fun remove(key: String?) {
        mSharedPreferences?.edit()?.remove(key)?.apply()
    }

    fun clear() {
        mSharedPreferences?.edit()?.clear()?.apply()
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