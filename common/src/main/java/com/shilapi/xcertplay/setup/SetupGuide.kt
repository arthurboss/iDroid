package com.shilapi.xcertplay.setup

import android.content.Context

/**
 * The first-run guide: choose how the iPhone connects, choose the iPhone, done.
 *
 * The same connection controls live in Settings → Connection, so the choice made here can be
 * changed at any time; nothing the guide sets is one-way.
 */
object SetupGuide {
    const val STEP_CONNECTION = 0
    const val STEP_IPHONE = 1
    const val STEP_DONE = 2
    const val STEP_COUNT = 3

    private fun prefs(context: Context) = context.getSharedPreferences("diplay", Context.MODE_PRIVATE)

    fun seen(context: Context): Boolean = prefs(context).getBoolean("setup_guide_seen", false)

    fun markSeen(context: Context) {
        prefs(context).edit().putBoolean("setup_guide_seen", true).apply()
    }

    /** Only a first launch opens the guide by itself; anyone with a saved iPhone has already set up. */
    fun shouldOpenOnLaunch(seen: Boolean, phoneChosen: Boolean): Boolean = !seen && !phoneChosen
}
