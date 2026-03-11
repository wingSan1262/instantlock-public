package com.risyan.quickshutdownphone.screen_content_guard.extensions
fun gradeFuzzyOccurrence(
    safeCount: Int,
    nsfwCount: Int,
    blankCount: Int,
    resetCounters: () -> Unit
): Boolean {

    val total = safeCount + nsfwCount + blankCount
    if (total < 24) return false

    val safe = safeCount / total.toFloat()
    val nsfw = nsfwCount / total.toFloat()
    val blank = blankCount / total.toFloat()

    // 1️⃣ Absolute NSFW presence
    val nsfwRisk = nsfw                       // 0..1

    // 2️⃣ NSFW >= SAFE parity trigger (critical)
    val parityRisk =
        if (nsfwCount >= safeCount && nsfwCount > 0) 0.35f else 0f

    // 3️⃣ Blank-to-safe dominance (incognito behavior)
    val blankSafeRatio =
        if (safeCount == 0) 1f
        else blankCount.toFloat() / safeCount.toFloat()

    val blankEvasionRisk =
        blankSafeRatio.coerceIn(0f, 1f)

    // 4️⃣ Absolute blank pressure (nonlinear)
    val blankPressure = blank * blank

    // 5️⃣ Final fuzzy score
    var score =
        nsfwRisk * 0.65f +
                blankEvasionRisk * 0.5f +
                blankPressure * 0.35f +
                parityRisk -
                safe * 0.1f

    score = score.coerceIn(0f, 1f)

    // smoothstep
    score = score * score * (3f - 2f * score)

    resetCounters()

    return score >= 0.6f
}
