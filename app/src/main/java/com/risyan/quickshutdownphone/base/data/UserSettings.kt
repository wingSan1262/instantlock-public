package com.risyan.quickshutdownphone.base.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

@Parcelize
data class UserLockSetting(
    var lockByNsfw: Boolean = true,
    var lockBySexy: Boolean = true,
    var nightTime: Boolean = false,
) : Parcelable

class UserSettingsApi(context: Context) {

    private val PREFS_NAME = "com.risyan.quickshutdownphone"
    private val LOCK_STATUS_KEY = "user_setting"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserSetting(userSetting: UserLockSetting) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(userSetting)
        editor.putString(LOCK_STATUS_KEY, json)
        editor.apply()
    }

    fun getUserSetting(): UserLockSetting {
        return try {
            val gson = Gson()
            val json = prefs.getString(LOCK_STATUS_KEY, null)
            gson.fromJson(json, UserLockSetting::class.java)
        } catch (e: Exception) {
            UserLockSetting()
        }
    }
}