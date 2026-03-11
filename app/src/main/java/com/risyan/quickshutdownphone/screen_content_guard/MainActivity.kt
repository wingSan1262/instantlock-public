package com.risyan.quickshutdownphone.screen_content_guard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.risyan.quickshutdownphone.base.base_class.BaseActivity
import com.risyan.quickshutdownphone.base.ui.theme.QuickShutdownPhoneTheme
import com.risyan.quickshutdownphone.screen_content_guard.navigator.OnboardNavigationHost


class MainActivity : BaseActivity() {
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickShutdownPhoneTheme {
                navController = rememberNavController()
                OnboardNavigationHost(nav = navController)
            }
        }
    }
}
