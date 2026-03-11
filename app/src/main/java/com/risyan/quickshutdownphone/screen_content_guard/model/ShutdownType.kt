package com.risyan.quickshutdownphone.screen_content_guard.model

import com.risyan.quickshutdownphone.MyApp
import com.risyan.quickshutdownphone.R

enum class ShutdownType(
    val duration: Int,
    val imageId: Int,
    val titleId: String,
    val messageId: String,
    val timeUntilLock: Int,
) {
    NONE(
        0,
        R.drawable.lock_meme,
        "", "",
        5000
    ),
    QUICK_3_MINUTES_NFSW(
//        5,
        3 * 60,
        R.drawable.lock_meme,
        MyApp.getInstance().getString(R.string.you_entered_the_forbidden_land),
        MyApp.getInstance().getString(R.string.denied_bonk),
        5000
    ),
    QUICK_5_MINUTES_NFSW(
//        5,
        5 * 60,
        R.drawable.lock_meme,
        MyApp.getInstance().getString(R.string.you_entered_the_forbidden_land),
        MyApp.getInstance().getString(R.string.denied_bonk),
        5000
    ),
    QUICK_3_MINUTES_INCOGNITO(
        3 * 60,
        R.drawable.neuron_buster,
        MyApp.getInstance().getString(R.string.woah_your_going_incognito_my_friend),
        MyApp.getInstance().getString(R.string.you_might_encounter_some),
        5000
    ),
    HORNY_1HOUR_LONG_TIME(
        60 * 60,
        R.drawable.neuron_buster,
        MyApp.getInstance().getString(R.string.let_s_take_a_break_ok),
        MyApp.getInstance().getString(R.string.it_s_been_harsh_there_let_s_take_a_break_from_taking_to_much_arousal),
        20000
    ),
    HORNY_20MINUTE_LONG_TIME(
        20 * 60,
        R.drawable.neuron_buster,
        MyApp.getInstance().getString(R.string.let_s_take_a_break_ok),
        MyApp.getInstance().getString(R.string.it_s_been_harsh_there_let_s_take_a_break_from_taking_to_much_arousal),
        20000
    ),
    NIGHT_4HOUR_TIME(
        4 * 60 * 60,
        R.drawable.lock_meme,
        MyApp.getInstance().getString(R.string.good_night),
        MyApp.getInstance().getString(R.string.throw_away_your_phone_and_have_a_good_night),
        20000
    )
}
