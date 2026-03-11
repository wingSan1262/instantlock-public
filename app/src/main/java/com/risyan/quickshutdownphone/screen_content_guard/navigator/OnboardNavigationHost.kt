package com.risyan.quickshutdownphone.screen_content_guard.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.risyan.quickshutdownphone.base.hasAllInstantLockPermission
import com.risyan.quickshutdownphone.screen_content_guard.screens.WelcomeScreen
import com.risyan.quickshutdownphone.screen_content_guard.screens.EduScreen
import com.risyan.quickshutdownphone.screen_content_guard.screens.MainSettingScreen
import com.risyan.quickshutdownphone.screen_content_guard.screens.SetupScreen

@Composable
fun OnboardNavigationHost(
    nav: NavHostController,
) {
    val navigator = remember(nav) {
        OnboardNavigator(nav)
    }
    val context = LocalContext.current

    NavHost(
        nav,
        startDestination = if (context.hasAllInstantLockPermission()) MAIN_SETTING_SCREEN
        else WELCOME_SCREEN
    ) {
        WelcomeScreen()
        EduScreen()
        SetupScreen()
        MainSettingScreen()
    }
}

val WELCOME_SCREEN by lazy { "WELCOME_SCREEN" }
val EDU_SCREEN by lazy { "EDU_SCREEN" }
val SETUP_PERMISSION by lazy { "SETUP_PERMISSION" }
val MAIN_SETTING_SCREEN by lazy { "MAIN_SETTING_SCREEN" }