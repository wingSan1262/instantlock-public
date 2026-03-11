package com.risyan.quickshutdownphone.screen_content_guard.navigator

import androidx.navigation.NavHostController

class OnboardNavigator(
    val nav : NavHostController
){

    companion object {
        var Current: OnboardNavigator? = null
    }
    init {
        Current = this
    }
    fun navigateToSetup() {
        nav.navigate(
            route = SETUP_PERMISSION
        )
    }

    fun navigateToEdu() {
        nav.navigate(
            route = EDU_SCREEN
        )
    }

    fun navigateToMainSettingPage() {
        nav.navigate(
            route = MAIN_SETTING_SCREEN
        )
    }
    fun pop(){
        nav.popBackStack()
    }
}