package com.risyan.quickshutdownphone.screen_content_guard.widget

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.risyan.quickshutdownphone.MyApp
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.checkJobAndSaveLockStatus
import com.risyan.quickshutdownphone.base.isNightTime
import com.risyan.quickshutdownphone.base.showSystemQuestionAnnouncement
import com.risyan.quickshutdownphone.base.startReplaceLockingAndNotify
import com.risyan.quickshutdownphone.databinding.CustomBonkDialogBinding
import com.risyan.quickshutdownphone.screen_content_guard.model.ShutdownType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun Context.startLockingReplaceExtreme(
    type: ShutdownType
){
    CoroutineScope(Dispatchers.Main).launch{
        startReplaceLockingAndNotify(
            MyApp.getInstance().sharedPrefApi, type
        )
    }
}

class CustomBonkDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val imageResource: Int = R.drawable.lock_meme,
    val onCancel: () -> Unit = {},
    val onStrongCasualLockDay: () -> Unit = {
        context.showSystemQuestionAnnouncement(
            "20 Minute Lock Down",
            "You're going 20 minute lockdown with 20 second gap time between lockdown. Are you sure",
            "Please Lock",
            "Cancel",
            {
                context.startLockingReplaceExtreme(ShutdownType.HORNY_1HOUR_LONG_TIME)
            }
        )
    },
    val onStrongCasualLockNight: () -> Unit = {
        context.showSystemQuestionAnnouncement(
            "1 Hour Lock Down",
            "You're going 1 hour lockdown with 20 second gap time between lockdown. Are you sure",
            "Please Lock",
            "Cancel",
            {
                context.startLockingReplaceExtreme(ShutdownType.HORNY_1HOUR_LONG_TIME)
            }
        )
    },
    val onNightTimeLock: () -> Unit = {
        context.showSystemQuestionAnnouncement(
            "4 Hour Lock Down",
            "You're going 4 hour lockdown with 20 second gap time between lockdown. Are you sure",
            "Please Lock",
            "Cancel",
            {
                context.startLockingReplaceExtreme(ShutdownType.NIGHT_4HOUR_TIME)
            }
        )
    }
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the view using view binding
        CustomBonkDialogBinding.inflate(
            LayoutInflater.from(context)
        ).apply {
            setContentView(root)
            dialogTitle.text = title
            dialogMessage.text = message
            dialogImage.setImageResource(imageResource)
            dialogButton.setOnClickListener {
                onCancel()
                dismiss()
            }
            extremeActionButtonDay.setOnClickListener {
                onStrongCasualLockDay()
                dismiss()
            }

            nightTimeLock.visibility = View.GONE
            extremeActionButtonNight.visibility = View.GONE

            if(isNightTime()){
                extremeActionButtonDay.visibility = View.GONE

                extremeActionButtonNight.visibility = View.VISIBLE
                extremeActionButtonNight.setOnClickListener {
                    onStrongCasualLockNight()
                    dismiss()
                }

                nightTimeLock.visibility = View.VISIBLE
                nightTimeLock.setOnClickListener {
                    onNightTimeLock()
                    dismiss()
                }
            }



        }

        // Set dialog background to be fully transparent
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Ensure layout params are applied
        val params = window?.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params?.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        window?.attributes = params

        this.setOnCancelListener {
            onCancel()
        }
    }
}
