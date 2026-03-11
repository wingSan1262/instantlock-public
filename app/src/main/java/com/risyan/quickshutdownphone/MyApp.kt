package com.risyan.quickshutdownphone

import android.app.Application
import com.risyan.quickshutdownphone.base.data.SharedPrefApi
import com.risyan.quickshutdownphone.base.data.UserSettingsApi
import kotlin.lazy

class MyApp : Application() {
    val sharedPrefApi: SharedPrefApi by lazy {
        SharedPrefApi(applicationContext)
    }

    val userLockSetting: UserSettingsApi by lazy {
        UserSettingsApi(applicationContext)
    }
    val userSetting by lazy {
        userLockSetting.getUserSetting()
    }

    override fun onCreate() {
        super.onCreate()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApp? = null

        fun getInstance(): MyApp =
            instance ?: throw IllegalStateException("MyApp has not been created yet")
    }
}