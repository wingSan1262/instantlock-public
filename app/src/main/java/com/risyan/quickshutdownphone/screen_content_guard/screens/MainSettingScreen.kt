package com.risyan.quickshutdownphone.screen_content_guard.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.risyan.quickshutdownphone.MyApp
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.checkJobAndSaveLockStatus
import com.risyan.quickshutdownphone.base.ui.theme.DateStyle
import com.risyan.quickshutdownphone.base.ui.theme.Header3Style
import com.risyan.quickshutdownphone.base.ui.theme.HeaderStyle
import com.risyan.quickshutdownphone.base.currentDatetoFormattedString
import com.risyan.quickshutdownphone.screen_content_guard.navigator.MAIN_SETTING_SCREEN
import com.risyan.quickshutdownphone.base.requestAccessibilityService
import com.risyan.quickshutdownphone.base.showSystemQuestionAnnouncement
import com.risyan.quickshutdownphone.base.openAdminPermissionSetting
import com.risyan.quickshutdownphone.screen_content_guard.service_utils.AccessibilityTemporaryShutoffChecker
import com.risyan.quickshutdownphone.base.showToast
import com.risyan.quickshutdownphone.base.startSingleAllBroadcastStarters
import com.risyan.quickshutdownphone.screen_content_guard.model.ShutdownType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun NavGraphBuilder.MainSettingScreen(){
    composable(route = MAIN_SETTING_SCREEN) {
        val rememberUserSetting = remember {
            MyApp.getInstance().userSetting
        }
        MainScreenContent(
            rememberUserSetting.lockByNsfw,
            rememberUserSetting.lockBySexy,
            rememberUserSetting.nightTime
        ){ nsfw, sexy, night ->
            val new = rememberUserSetting.copy(
                lockByNsfw = nsfw,
                lockBySexy = sexy,
                nightTime = night
            )
            MyApp.getInstance().userLockSetting.saveUserSetting(
                new
            )
            MyApp.getInstance().userSetting.apply {
                lockByNsfw = nsfw
                lockBySexy = sexy
                nightTime = night
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MainScreenContent(
    nsfwMode: Boolean = true,
    sexyMode: Boolean = true,
    nightTimeMode : Boolean = false,
    onUpdate: (nfsw: Boolean, sexy: Boolean, night: Boolean) -> Unit = { _, _, _ -> }
) {

    var scope = rememberCoroutineScope()
    var context = LocalContext.current
    var accessibilityTemporaryShutoffChecker by remember { mutableStateOf(
        AccessibilityTemporaryShutoffChecker(
            MyApp.getInstance().sharedPrefApi,
            context
        )
    ) }
    var nsfwLockMode by remember { mutableStateOf(nsfwMode) }
    var sexyLockMode by remember { mutableStateOf(sexyMode) }
    var nightTimeLockMode by remember { mutableStateOf(nightTimeMode) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_monochrome),
                contentDescription = "app_icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = Header3Style
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = currentDatetoFormattedString(),
            style = DateStyle
        )
        Spacer(modifier = Modifier.height(32.dp))

        SwitchSetting(
            label = stringResource(R.string.sexy_lock_mode),
            isChecked = sexyLockMode,
            onCheckedChange = {
                sexyLockMode = it
                onUpdate(nsfwLockMode, it, nightTimeLockMode)
            }
        )

        SwitchSetting(
            label = stringResource(R.string.night_time_lock_mode),
            isChecked = nightTimeLockMode,
            onCheckedChange = {
                onUpdate(nsfwLockMode, sexyLockMode, it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.please_choose_action_below),
            style = DateStyle.copy(
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = stringResource(R.string.temporary_turn_off_accessibility_service), style = TextStyle.Default)
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                context.showToast(context.getString(R.string.please_turn_off_accessibility_service_the_app_will_help_to_turn_it_back_on_later))
                context.requestAccessibilityService()
                accessibilityTemporaryShutoffChecker.
                    initiateAccessibilityTemporaryShutoffChecker(true)
                scope.launch {
                    delay(5000)
                    context.startSingleAllBroadcastStarters()
                }
            }) {
                Text(stringResource(R.string.turn_off))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = stringResource(R.string.panic_1_hour_lock), style = TextStyle.Default)
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                context.showSystemQuestionAnnouncement(
                    context.getString(R.string._1_hour_lock_down),
                    context.getString(R.string.you_re_going_1_hour_lockdown_with_30_second_gap_time_between_lockdown_are_you_sure),
                    context.getString(R.string.please_lock),
                    context.getString(R.string.cancel),
                    {
                        context.checkJobAndSaveLockStatus(ShutdownType.HORNY_1HOUR_LONG_TIME, MyApp.getInstance().sharedPrefApi)
                    }
                )
            }) {
                Text(stringResource(id = R.string.turn_off))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Uninstall",
            style = DateStyle.copy(
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Open Device Admin to Uninstall",
                style = TextStyle.Default
            )
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                context.showToast("Please deactivate 'Device Administrator' first, then you can uninstall the app from your device settings")
                context.openAdminPermissionSetting()
            }) {
                Text("Open Settings")
            }
        }
    }
}

@Composable
fun SwitchSetting(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = HeaderStyle
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}