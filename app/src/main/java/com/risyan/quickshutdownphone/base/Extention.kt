package com.risyan.quickshutdownphone.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.data.LockStatus
import com.risyan.quickshutdownphone.base.data.SharedPrefApi
import com.risyan.quickshutdownphone.screen_content_guard.model.ShutdownType
import com.risyan.quickshutdownphone.screen_content_guard.receivers.MyAdmin
import com.risyan.quickshutdownphone.screen_content_guard.receivers.NightTimeReceiver
import com.risyan.quickshutdownphone.screen_content_guard.receivers.UserUnlockReceiver
import com.risyan.quickshutdownphone.screen_content_guard.services.LockdownAcessibilityService
import com.risyan.quickshutdownphone.screen_content_guard.services.ReceiverSetupService
import com.risyan.quickshutdownphone.screen_content_guard.widget.CustomBonkDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.min


fun Context.showToast(message: String) {
    android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
}

fun getCurrentDatePlusSeconds(seconds: Int): Long {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.SECOND, seconds)
    return calendar.timeInMillis
}


fun Bitmap.cropCenterShavedSquare(ratio: Float = 0.75f): Bitmap {
    require(ratio in 0f..1f)

    val size = min(width, height)
    val shavedSize = (size * ratio).toInt()

    val left = (width - shavedSize) / 2
    val top = (height - shavedSize) / 2

    return Bitmap.createBitmap(
        this,
        left,
        top,
        shavedSize,
        shavedSize
    )
}

fun Bitmap.cropTopSquare(): Bitmap {
    val bitmap = this
    val dimension = min(bitmap.width, bitmap.height)
    return Bitmap.createBitmap(bitmap, 0, 0, dimension, dimension)
}

fun Bitmap.cropBottomSquare(): Bitmap {
    val bitmap = this
    val dimension = min(bitmap.width, bitmap.height)
    val yOffset = bitmap.height - dimension
    return Bitmap.createBitmap(bitmap, 0, yOffset, dimension, dimension)
}

fun Bitmap.cropCenterSquare(): Bitmap {
    val bitmap = this
    val dimension = min(bitmap.width, bitmap.height)
    val xOffset = (bitmap.width - dimension) / 2
    val yOffset = (bitmap.height - dimension) / 2
    return Bitmap.createBitmap(bitmap, xOffset, yOffset, dimension, dimension)
}

fun getSecondsDiff(date1: Long, date2: Long): Long {
    val diffInMillis = Math.abs(date1 - date2)
    return TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
}

@SuppressLint("MissingPermission")
fun Context.showNotification(message: String): Notification {
    val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
        .setSmallIcon(R.mipmap.ic_launcher_monochrome)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(this)) {
        val notificationId = 1
        val notification = builder.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "CHANNEL_ID",
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = getString(R.string.channel_description)
            this.createNotificationChannel(channel)
        }

        notify(notificationId, notification)
        return@showNotification notification
    }
}

fun Context.checkJobAndSaveLockStatus(
    type: ShutdownType,
    sharedPrefApi: SharedPrefApi,
    job: Job? = null,
    delay: Long = 2000,
): Job {
    job?.cancel()
    return CoroutineScope(Dispatchers.Main).launch {
        delay(delay)
        startLockingAndNotify(sharedPrefApi, type)
    }
}


fun SharedPrefApi.isAnyActiveLock(): Boolean {
    val lockStatus = getLockStatus()
    return lockStatus?.startLock == true
}


suspend fun Context.showDialogAndInitLock(
    sharedPrefApi: SharedPrefApi,
    type: ShutdownType,
    duration: Int = type.duration,
){
    val newLockStatus = LockStatus(
        true,
        getCurrentDatePlusSeconds(duration)
    )
    lockDialogGlobal = CustomBonkDialog(
        this,
        type.titleId,
        type.messageId +
                "\n\n" +
                getString(R.string.punishment_time_remaining) + newLockStatus.getRemainingDurationTo(
            this
        ),
        type.imageId
    )
    sharedPrefApi.saveLockStatus(
        newLockStatus
    )
    lockDialogGlobal?.show()
    delay(type.timeUntilLock.toLong())
    lockDialogGlobal?.dismiss()
    doLock()
}

fun isNightTime(): Boolean {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return (hour == 21 && minute >= 30) || (hour == 22) || (hour == 23)
}

suspend fun Context.startReplaceLockingAndNotify(
    sharedPrefApi: SharedPrefApi,
    type: ShutdownType,
    duration: Int = type.duration,
) {

    lockDialogGlobal?.dismiss()
    if(sharedPrefApi.isAnyActiveLock()){
        sharedPrefApi.removeLockStatus()
    }

    showDialogAndInitLock(
        sharedPrefApi,
        type,
        duration
    )

}

suspend fun Context.startLockingAndNotify(
    sharedPrefApi: SharedPrefApi,
    type: ShutdownType,
    duration: Int = type.duration,
) {

    lockDialogGlobal?.dismiss()
    if(sharedPrefApi.isAnyActiveLock()){
        reLockAndNotifyOrRemoveIfExpired(sharedPrefApi)
        return
    }

    showDialogAndInitLock(
        sharedPrefApi,
        type,
        duration
    )
}

fun Context.startSingleAllBroadcastStarters() {
    val serviceIntent = Intent(this, ReceiverSetupService::class.java)
    stopService(serviceIntent)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(serviceIntent)
    } else {
        this.startService(serviceIntent)
    }
}


fun Service.startServiceForeground() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        startForeground(1, this.showNotification(getString(R.string.instant_lockdown_is_ready)),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST)
    } else {
        startForeground(1, this.showNotification(getString(R.string.instant_lockdown_is_ready)))
    }
}

fun Service.startUnlockReceiverByStartForeground(): UserUnlockReceiver {
    startServiceForeground()
    val userUnlockReceiver = UserUnlockReceiver()
    val filter = IntentFilter().apply {
        addAction(Intent.ACTION_SCREEN_OFF)
        addAction(Intent.ACTION_SCREEN_ON)
        addAction(Intent.ACTION_USER_PRESENT)
    }
    registerReceiver(userUnlockReceiver, filter)
    return userUnlockReceiver;
}

fun Context.scheduleNightTimeLock() {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(this, NightTimeReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 21)
        set(Calendar.MINUTE, 30)
    }

    alarmManager.setInexactRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

var incognitoGlobalDialog : Dialog? = null
suspend fun Context.showIncognitoWarning(
    onDismiss: (manualDismiss: Boolean) -> Unit
){
    if(incognitoGlobalDialog != null){
        return
    }

    var isManualDismissed = true
    incognitoGlobalDialog = CustomBonkDialog(
        this,
        getString(R.string.hey_what_are_you_doing),
        getString(R.string.you_better_not_do_anything_suspicious_long_incognito_or_you_ll_be_bonked_from_this_phone),
        R.drawable.lock_meme,
        onCancel = {
            onDismiss(isManualDismissed)
            incognitoGlobalDialog = null
        }
    )
    incognitoGlobalDialog?.show()
    delay(15000)
    isManualDismissed = false
    incognitoGlobalDialog?.dismiss()
    incognitoGlobalDialog = null
}

var lockDialogGlobal: Dialog? = null
suspend fun Context.reLockAndNotifyOrRemoveIfExpired(
    sharedPrefApi: SharedPrefApi,
) {
    val lockStatus = sharedPrefApi.getLockStatus()
    lockDialogGlobal?.dismiss()
    if (lockStatus?.startLock == true && lockStatus.endLock > System.currentTimeMillis()) {
        lockDialogGlobal = CustomBonkDialog(
            this,
            getString(R.string.you_are_being_denied_from_this_phone),
            getString(R.string.you_wandered_too_far)
                    + lockStatus.getRemainingDurationTo(this),
            R.drawable.lockdown_notice
        )
        lockDialogGlobal?.show()
        delay(5000)
        lockDialogGlobal?.dismiss()
        doLock()
        return
    }
    sharedPrefApi.removeLockStatus()
}

fun Long.toMinuteAndSecondFormat(
    context: Context
): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    //String.format("%02d minutes and %02d seconds", minutes, seconds)
    return minutes.toString() + context.getString(R.string.minutes_and_label) + seconds.toString() + " seconds"
}

fun Context.showInAppAnnouncement(
    title: String,
    message: String,
    positiveButton: String,
    onDismiss: (() -> Unit) = {}
): AlertDialog {
    val builder = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButton) { dialog, _ -> dialog.dismiss() }
        .setOnDismissListener { onDismiss() }

    val dialog = builder.create()
    dialog.show()
    return dialog
}


fun Context.showSystemQuestionAnnouncement(
    title: String,
    message: String,
    positiveButton: String,
    negativeButton: String,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
): AlertDialog? {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(positiveButton) { dialog, _ ->
        onPositiveClick.invoke()
        dialog.dismiss()
    }
    builder.setNegativeButton(negativeButton) { dialog, _ ->
        onNegativeClick.invoke()
        dialog.dismiss()
    }

    builder.setOnDismissListener { onDismiss.invoke() }
    val dialog = builder.create()
    val params = dialog.window?.attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        params?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        params?.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    }
    dialog.window?.attributes = params
    dialog.show()
    return dialog
}
fun Context.showSystemAnnouncement(
    title: String,
    message: String,
    positiveButton: String,
    onDismiss: (() -> Unit) = { }
): AlertDialog? {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton(positiveButton) { dialog, _ -> dialog.dismiss() }
    builder.setOnDismissListener { onDismiss.invoke() }
    val dialog = builder.create()
    val params = dialog.window?.attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        params?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        params?.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    }
    dialog.window?.attributes = params
    dialog.show()
    return dialog
}


fun Context.doLock() {
    val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(this, MyAdmin::class.java)

    val active = devicePolicyManager.isAdminActive(compName)
    if (active) {
        devicePolicyManager.lockNow()
    }
}

fun Context.hasNotificationAccess(): Boolean {
    if (Build.VERSION.SDK_INT >= 33) {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
    return true
}

fun Context.openOverlayPermissionSetting() {
    val intent =
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
    startActivity(intent)
}

fun Context.hasOverlayPermission(): Boolean {
    return Settings.canDrawOverlays(this)
}

fun Context.hasAllInstantLockPermission() = hasAdminPermission() && hasNotificationAccess() &&
        hasAccessibilityService() && hasOverlayPermission() &&
        hasStorageAccessNeededInstantLock()

fun Context.hasStorageAccessNeededInstantLock(): Boolean {
    return hasStorageAccess() ||
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
}

fun Context.showInAppQuestionDialog(
    title: String,
    message: String,
    positiveButton: String,
    negativeButton: String,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
): Dialog {
    val builder = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton(positiveButton) { dialog, _ ->
            onPositiveClick()
            dialog.dismiss()
        }
        .setNegativeButton(negativeButton) { dialog, _ ->
            onNegativeClick()
            dialog.dismiss()
        }
        .setOnDismissListener {
            onDismiss()
        }

    val dialog = builder.create()
    dialog.show()
    return dialog
}

fun Context.hasStorageAccess(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.requestStorageAccess() {
    ActivityCompat.requestPermissions(
        this as ComponentActivity,
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ),
        101
    )
}

fun Context.hasAccessibilityService(): Boolean {
    val service = ComponentName(this, LockdownAcessibilityService::class.java)
    val accessibilityManager =
        getSystemService(Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
    val enabledServices =
        Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(service.flattenToString()) == true
}

fun Context.requestAccessibilityService() {
    // NEW TASK

    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.requestNotificationAccess() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            this as ComponentActivity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            101
        )
    };
}

fun Context.openAdminPermissionSetting() {
    val intent = Intent().apply {
        setComponent(
            ComponentName(
                "com.android.settings",
                "com.android.settings.DeviceAdminSettings"
            )
        )
    }
    startActivity(intent)
}

fun Context.hasAdminPermission(): Boolean {
    val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(this, MyAdmin::class.java)
    return devicePolicyManager.isAdminActive(compName)
}


@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

fun currentDatetoFormattedString(): String {
    val date = Date()
    val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

fun Context.isVivoDevice(): Boolean {
    return Build.MANUFACTURER.equals("vivo", ignoreCase = true)
}

// is xiaomi device
fun Context.isXiaomiDevice(): Boolean {
    return Build.MANUFACTURER.equals("xiaomi", ignoreCase = true)
}

// is huawei device
fun Context.isHuaweiDevice(): Boolean {
    return Build.MANUFACTURER.equals("huawei", ignoreCase = true)
}

// is oppo device
fun Context.isOppoDevice(): Boolean {
    return Build.MANUFACTURER.equals("oppo", ignoreCase = true)
}

// is letv device
fun Context.isLetvDevice(): Boolean {
    return Build.MANUFACTURER.equals("letv", ignoreCase = true)
}

// is honor device
fun Context.isHonorDevice(): Boolean {
    return Build.MANUFACTURER.equals("honor", ignoreCase = true)
}

fun Context.isManufactureAdditionalSetting(): Boolean {
    return isVivoDevice() || isXiaomiDevice() || isHuaweiDevice() ||
            isOppoDevice() || isLetvDevice() || isHonorDevice()
}

// open xiaomi auto start setting
fun Context.autoLaunchXiaomiSetting() {
    try {
        val intent = Intent()
        intent.component = ComponentName(
            "com.miui.securitycenter",
            "com.miui.permcenter.autostart.AutoStartManagementActivity"
        )
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// open letv auto start setting
fun Context.autoLaunchLetvSetting() {
    try {
        val intent = Intent()
        intent.component = ComponentName(
            "com.letv.android.letvsafe",
            "com.letv.android.letvsafe.AutobootManageActivity"
        )
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// open honor auto start setting
fun Context.autoLaunchHonorSetting() {
    try {
        val intent = Intent()
        intent.component = ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.systemmanager.optimize.process.ProtectActivity"
        )
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// open oppo auto start setting
fun Context.autoLaunchOppoSetting() {
    try {
        val intent = Intent()
        intent.component = ComponentName(
            "com.coloros.safecenter",
            "com.coloros.safecenter.permission.startup.StartupAppListActivity"
        )
        startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent()
            intent.component = ComponentName(
                "com.oppo.safe",
                "com.oppo.safe.permission.startup.StartupAppListActivity"
            )
            startActivity(intent)
        } catch (ex: Exception) {
            try {
                val intent = Intent()
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.startupapp.StartupAppListActivity"
                )
                startActivity(intent)
            } catch (exx: Exception) {
                ex.printStackTrace()
            }
        }
    }
}

// Open Setting
fun Context.autoLaunchVivoSetting() {
    try {
        val intent = Intent()
        intent.setComponent(
            ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"
            )
        )
        startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent()
            intent.setComponent(
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            )
            startActivity(intent)
        } catch (ex: Exception) {
            try {
                val intent = Intent()
                intent.setClassName(
                    "com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"
                )
                startActivity(intent)
            } catch (exx: Exception) {
                ex.printStackTrace()
            }
        }
    }
}

// open url
fun Context.openUrl(url: String) {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse(url)
    startActivity(i)
}
