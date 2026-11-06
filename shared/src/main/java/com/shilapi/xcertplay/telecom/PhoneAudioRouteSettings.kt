package com.shilapi.xcertplay.telecom

import android.content.Context

/**
 * Opt-ins for handing a call's audio to the device's own telephony stack instead of DiPlay's
 * mixer, so that its echo canceller and audio state handle the call. Both are off until the
 * owner turns them on, and each applies at the next call.
 */
object PhoneAudioRouteSettings {
    private const val PREFS = "diplay_features"
    private const val KEY_CARPLAY_CALLS = "phone_audio_route_experimental"
    private const val KEY_VOIP_APPS = "phone_audio_route_apps_experimental"

    /** Hold a Telecom call during CarPlay calls so the device's phone audio path carries it. */
    fun carPlayCalls(context: Context): Boolean = prefs(context).getBoolean(KEY_CARPLAY_CALLS, false)

    fun setCarPlayCalls(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_CARPLAY_CALLS, enabled).apply()

    /** The same for voice-over-IP calls in other apps; only calls recording from VOICE_COMMUNICATION qualify. */
    fun voipApps(context: Context): Boolean = prefs(context).getBoolean(KEY_VOIP_APPS, false)

    fun setVoipApps(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_VOIP_APPS, enabled).apply()

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
