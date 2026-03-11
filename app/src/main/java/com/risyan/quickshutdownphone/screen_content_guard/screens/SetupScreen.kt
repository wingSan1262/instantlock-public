package com.risyan.quickshutdownphone.screen_content_guard.screens

import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.ui.theme.Green_4CAF50
import com.risyan.quickshutdownphone.base.OnLifecycleEvent
import com.risyan.quickshutdownphone.base.hasAccessibilityService
import com.risyan.quickshutdownphone.base.hasAdminPermission
import com.risyan.quickshutdownphone.base.hasNotificationAccess
import com.risyan.quickshutdownphone.base.hasOverlayPermission
import com.risyan.quickshutdownphone.base.hasStorageAccessNeededInstantLock
import com.risyan.quickshutdownphone.screen_content_guard.navigator.OnboardNavigator
import com.risyan.quickshutdownphone.screen_content_guard.navigator.SETUP_PERMISSION
import com.risyan.quickshutdownphone.base.openAdminPermissionSetting
import com.risyan.quickshutdownphone.base.openOverlayPermissionSetting
import com.risyan.quickshutdownphone.base.requestAccessibilityService
import com.risyan.quickshutdownphone.base.requestNotificationAccess
import com.risyan.quickshutdownphone.base.requestStorageAccess
import com.risyan.quickshutdownphone.base.showInAppAnnouncement
import com.risyan.quickshutdownphone.base.showInAppQuestionDialog
import com.risyan.quickshutdownphone.base.showSystemAnnouncement
import com.risyan.quickshutdownphone.base.showToast
import com.risyan.quickshutdownphone.screen_content_guard.widget.AutoSpecificInstruction
import kotlin.system.exitProcess

fun NavGraphBuilder.SetupScreen() {
    composable(route = SETUP_PERMISSION) {
        val context = LocalContext.current
        SetupContent(
            isNeedNotificaiton = SDK_INT >= Build.VERSION_CODES.TIRAMISU,
            isHaveNotificationAccess = context.hasNotificationAccess(),
            isHaveAdminAccess = context.hasAdminPermission(),
            isHaveAcessibiltyService = context.hasAccessibilityService(),
            isHaveStorageAccess = context.hasStorageAccessNeededInstantLock(),
            isHaveOverlayAccess = context.hasOverlayPermission()
        )
    }
}

@Composable
@Preview
fun SetupContent(
    context: Context = LocalContext.current,
    isNeedNotificaiton: Boolean = false,
    isHaveNotificationAccess: Boolean = false,
    isHaveAdminAccess: Boolean = false,
    isHaveAcessibiltyService: Boolean = false,
    isHaveStorageAccess: Boolean = false,
    isHaveOverlayAccess: Boolean = false,
) {

    var isHaveNotificationPermission by remember {
        mutableStateOf(isHaveNotificationAccess)
    }

    var isHaveStoragePermission by remember {
        mutableStateOf(isHaveStorageAccess)
    }

    var isHaveAccessibiltyServicePermission by remember {
        mutableStateOf(isHaveAcessibiltyService)
    }

    var isHaveAdminPermission by remember {
        mutableStateOf(isHaveAdminAccess)
    }

    var isHaveOverlayPermission by remember {
        mutableStateOf(isHaveOverlayAccess)
    }


    OnLifecycleEvent { owner, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            context.hasNotificationAccess().also { isHaveNotificationPermission = it }
            context.hasAdminPermission().also { isHaveAdminPermission = it }
            context.hasAccessibilityService().also { isHaveAccessibiltyServicePermission = it }
            context.hasStorageAccessNeededInstantLock().also { isHaveStoragePermission = it }
            context.hasOverlayPermission().also { isHaveOverlayPermission = it }
        }
    }

    Column {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.setup_access_and_permission),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.lockdown_instruction_title),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (isNeedNotificaiton) ListItem(
            headlineContent = {
                Text(
                    if (isHaveNotificationPermission) stringResource(R.string.notification_permission_granted)
                    else stringResource(R.string.need_notification_permission)
                )
            },
            trailingContent = {
                if (isHaveNotificationPermission) Icon(
                    Icons.Default.Check,
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Green_4CAF50),
                    contentDescription = null,
                )
                else Button(onClick = {
                    context.requestNotificationAccess()
                }) {
                    Text(text = stringResource(R.string.grant))
                }
            },
        )


        ListItem(
            headlineContent = {
                Text(
                    if (isHaveOverlayPermission) stringResource(id = R.string.overlay_permission_granted)
                    else stringResource(id = R.string.need_overlay_permission)
                )
            },
            trailingContent = {
                if (isHaveOverlayPermission) Icon(
                    Icons.Default.Check,
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Green_4CAF50),
                    contentDescription = null,
                )
                else Button(onClick = {
                    context.showInAppQuestionDialog(
                        context.getString(R.string.overlay_permission),
                        context.getString(R.string.this_permission_needed_to_show_lock_notification),
                        positiveButton = context.getString(R.string.consenting),
                        negativeButton = context.getString(R.string.not_consenting),
                        {
                            context.openOverlayPermissionSetting()
                        },
                        {
                            context.showToast("Please grant overlay permission")
                        },
                        {}
                    )
                }) {
                    Text(text = stringResource(id = R.string.grant))
                }
            },
        )

        ListItem(
            headlineContent = {
                Text(
                    if (isHaveStoragePermission) stringResource(R.string.storage_permission_granted)
                    else stringResource(R.string.need_storage_permission)
                )
            },
            trailingContent = {
                if (isHaveStoragePermission) Icon(
                    Icons.Default.Check,
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Green_4CAF50),
                    contentDescription = null,
                )
                else Button(onClick = {
                    context.showInAppQuestionDialog(
                        context.getString(R.string.storage_permission),
                        context.getString(R.string.this_permission_needed_to_obtain_screen_activity),
                        positiveButton = context.getString(R.string.consenting),
                        negativeButton = context.getString(R.string.not_consenting),
                        {context.requestStorageAccess()},
                        {
                            context.showToast("Please grant storage permission")
                        },
                        {}
                    )
                }) {
                    Text(text = stringResource(id = R.string.grant))
                }
            },
        )



        ListItem(
            headlineContent = {
                Text(
                    if (isHaveAdminPermission) stringResource(R.string.admin_permission_granted)
                    else stringResource(R.string.need_admin_permission)
                )
            },
            trailingContent = {
                if (isHaveAdminPermission) Icon(
                    Icons.Default.Check,
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Green_4CAF50),
                    contentDescription = null,
                )
                else Button(onClick = {
                    context.showInAppQuestionDialog(
                        title = context.getString(R.string.admin_permission_title),
                        message = context.getString(R.string.admin_permission_message),
                        positiveButton = context.getString(R.string.consenting),
                        negativeButton = context.getString(R.string.not_consenting),
                        {context.openAdminPermissionSetting()},
                        {
                            context.showToast("Please grant device admin so app can lock device temporarily when needed")
                            exitProcess(0)
                        },
                        {}
                    )
                }) {
                    Text(text = stringResource(id = R.string.grant))
                }
            },
        )

        ListItem(
            headlineContent = {
                Text(
                    if (isHaveAccessibiltyServicePermission) stringResource(R.string.accessibility_service_permission_granted)
                    else stringResource(R.string.need_accessibility_service_permission)
                )
            },
            trailingContent = {
                if (isHaveAccessibiltyServicePermission) Icon(
                    Icons.Default.Check,
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Green_4CAF50),
                    contentDescription = null,
                )
                else Button(onClick = {
                    if(context.hasStorageAccessNeededInstantLock()){
                        context.showInAppQuestionDialog(
                            title = context.getString(R.string.accesibility_api_title),
                            message = context.getString(R.string.accesibility_api_message),
                            positiveButton = context.getString(R.string.consenting),
                            negativeButton = context.getString(R.string.not_consenting),
                            {context.requestAccessibilityService()},
                            {
                                context.showToast("Please grant accessibility api so app can work properly")
                                exitProcess(0)
                            },
                            {}
                        )
                    }else {
                        Toast.makeText(context,
                            context.getString(R.string.please_grant_storage_permission_first), Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = stringResource(id = R.string.grant))
                }
            },
        )

        AutoSpecificInstruction{ isAutoStartSpecDevice ->
            if(!isHaveAdminPermission || !isHaveOverlayPermission || !isHaveAccessibiltyServicePermission){
                context.showToast(context.getString(R.string.please_grant_all_permission_first))
                return@AutoSpecificInstruction
            }

            if(isAutoStartSpecDevice){
                context.showInAppQuestionDialog(
                    title = context.getString(R.string.auto_start_permission_necessity),
                    message = context.getString(R.string.without_autostart_instant_lockdown),
                    positiveButton = context.getString(R.string.i_already_grant),
                    negativeButton = context.getString(R.string.set_autostart_first),
                    {OnboardNavigator.Current?.navigateToMainSettingPage()},
                    {},
                    {}
                )
                return@AutoSpecificInstruction
            }
            OnboardNavigator.Current?.navigateToMainSettingPage()
        }

    }
}