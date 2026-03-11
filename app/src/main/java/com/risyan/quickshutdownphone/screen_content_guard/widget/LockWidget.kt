package com.risyan.quickshutdownphone.screen_content_guard.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.risyan.quickshutdownphone.databinding.LockWidgetOverlayBinding
import com.risyan.quickshutdownphone.screen_content_guard.model.ShutdownType
import kotlinx.coroutines.*


@SuppressLint("ClickableViewAccessibility")
class LockWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    val on2SecondHold : () -> Unit,
    val on4SecondHold : () -> Unit,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding: LockWidgetOverlayBinding
    var isHolding = false
    private var holdJob: Job? = null
    private var progressJob: Job? = null
    private var holdType = ShutdownType.NONE

    private var translucentJob: Job? = null
    fun setNotTranslucentForAMoment(){
        translucentJob?.cancel()
        translucentJob = CoroutineScope(Dispatchers.Main).launch {
            binding.cardView.alpha = 1f
            delay(2000)
            binding.cardView.alpha = 0.25f
        }
    }

    fun onActionDown() {
        if(!isHolding){
            isHolding = true
            listenForHold()
        }
    }
    fun onActionUp() {
        if(isHolding){
            setProgressBarColor(Color.parseColor("#4DA5E4"))

            stopProgress()
            holdJob?.cancel()
            if(holdType == ShutdownType.QUICK_5_MINUTES_NFSW){
                on2SecondHold()
            }
            holdType = ShutdownType.NONE
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                isHolding = false
            }
        }

    }
    init {
        val inflater = LayoutInflater.from(context)
        binding = LockWidgetOverlayBinding.inflate(inflater, this, true)
    }

    fun listenForHold(){
        holdJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(startProgress(1500).toLong())
                holdType = ShutdownType.QUICK_5_MINUTES_NFSW
                setProgressBarColor(Color.RED)
                delay(startProgress(2000).toLong())
                delay(startProgress(2000).toLong())
                holdType = ShutdownType.NONE
                return@launch
            }
        }
    }

    fun setProgressBarColor(color: Int) {
        binding.progressCircular.progressTintList = ColorStateList.valueOf(color)
    }

    private fun startProgress(
        progressTime : Int,
    ): Int {
        val step = 1
        val delayMillis = progressTime / 100;
        binding.progressCircular.max = 100
        binding.progressCircular.progress = 0
        stopProgress()
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && binding.progressCircular.progress < 100) {
                binding.progressCircular.progress += step
                delay(delayMillis.toLong())
            }
        }
        return progressTime+200
    }

    private fun stopProgress() {
        progressJob?.cancel()
        binding.progressCircular.progress = 0
    }
}