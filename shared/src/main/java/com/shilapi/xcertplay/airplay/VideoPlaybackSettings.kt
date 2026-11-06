package com.shilapi.xcertplay.airplay

import android.content.Context

/**
 * The owner's opt-in for iOS 27 "video in car" (see [VideoInCar]).
 *
 * The iPhone offers the feature only to a receiver that reports a stopped vehicle. A tablet has no
 * gear or speed signal to report, so this switch is the whole answer and stays off until the owner
 * turns it on. Leave it off wherever the screen is visible to a driver.
 */
object VideoPlaybackSettings {
    private const val PREFS = "diplay_features"
    private const val KEY_ALLOWED = "video_playback_allowed"

    fun allowed(context: Context): Boolean = prefs(context).getBoolean(KEY_ALLOWED, false)

    fun setAllowed(context: Context, allowed: Boolean) {
        prefs(context).edit().putBoolean(KEY_ALLOWED, allowed).commit()
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
