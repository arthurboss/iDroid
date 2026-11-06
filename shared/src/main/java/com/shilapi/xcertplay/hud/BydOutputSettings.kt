package com.shilapi.xcertplay.hud

import android.content.Context
import com.shilapi.xcertplay.transport.EvChargingConnectors

/**
 * Opt-in switches for the optional features that read vehicle data over ADB or change how calls
 * are routed. Each one is off until the owner turns it on.
 */
object BydOutputSettings {
    private const val PREFS = "diplay_byd_outputs"
    private const val KEY_BATTERY_TO_IPHONE = "battery_to_iphone"
    private const val KEY_LOW_CHARGE_PERCENT = "low_charge_percent"
    private const val KEY_CHARGING_CONNECTORS = "charging_connectors"
    private const val KEY_WHEEL_SPEED_TO_IPHONE = "wheel_speed_to_iphone"
    private const val KEY_VIDEO_WHILE_PARKED = "video_while_parked"
    private const val KEY_CARPLAY_CALLS = "carplay_calls"
    private const val KEY_CARPLAY_CALL_CONTROLS = "carplay_call_controls_experimental"
    private const val KEY_PHONE_AUDIO_ROUTE = "phone_audio_route_experimental"
    private const val KEY_PHONE_AUDIO_ROUTE_APPS = "phone_audio_route_apps_experimental"
    private const val KEY_LEGACY_VEHICLE_PROBE = "legacy_vehicle_probe"
    const val DEFAULT_LOW_CHARGE_PERCENT = 20
    val lowChargePresets = listOf(10, 15, 20, 25, 30)


    /** Tell the iPhone the car's charge and range (needs ADB over network); applies on the next connection. */
    fun batteryToIphone(context: Context): Boolean = prefs(context).getBoolean(KEY_BATTERY_TO_IPHONE, false)

    fun setBatteryToIphone(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_BATTERY_TO_IPHONE, enabled).commit()
    }

    /** Default mode uses the DiLink 5.0 addresses; legacy mode exposes only fields its saved probe confirmed. */
    fun batteryToIphoneActive(context: Context): Boolean =
        batteryToIphone(context) && supportedInSelectedMode(context) { it.batterySupported }

    /** The charging inlets the iPhone is told about; applies on the next connection. */
    fun chargingConnectors(context: Context): EvChargingConnectors =
        prefs(context).getString(KEY_CHARGING_CONNECTORS, null)
            ?.let { saved -> EvChargingConnectors.entries.firstOrNull { it.name == saved } }
            ?: EvChargingConnectors.CCS2_TYPE2

    fun setChargingConnectors(context: Context, connectors: EvChargingConnectors) {
        prefs(context).edit().putString(KEY_CHARGING_CONNECTORS, connectors.name).commit()
    }

    /** Send wheel speed and gear with the car's GPS (needs ADB over network); applies on the next connection. */
    fun wheelSpeedToIphone(context: Context): Boolean = prefs(context).getBoolean(KEY_WHEEL_SPEED_TO_IPHONE, false)

    fun setWheelSpeedToIphone(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_WHEEL_SPEED_TO_IPHONE, enabled).commit()
    }

    fun wheelSpeedToIphoneActive(context: Context): Boolean =
        wheelSpeedToIphone(context) && supportedInSelectedMode(context) { it.motionSupported }
    /** Offer iOS 27 video in car, played only while the gear reads P (needs ADB over network). */
    fun videoWhileParked(context: Context): Boolean = prefs(context).getBoolean(KEY_VIDEO_WHILE_PARKED, false)

    fun setVideoWhileParked(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_VIDEO_WHILE_PARKED, enabled).commit()
    }


    /** Show CarPlay calls on the dashboard and HUD like BYD's CarPlay app (needs ADB over network); applies at once. */
    fun carPlayCalls(context: Context): Boolean = prefs(context).getBoolean(KEY_CARPLAY_CALLS, false)

    fun setCarPlayCalls(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_CARPLAY_CALLS, enabled).apply()

    /** Unverified DiLink 3 call/voice/media key handling requires a separate explicit opt-in. */
    fun carPlayCallControls(context: Context): Boolean =
        prefs(context).getBoolean(KEY_CARPLAY_CALL_CONTROLS, false)

    fun setCarPlayCallControls(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_CARPLAY_CALL_CONTROLS, enabled).apply()

    /**
     * Hold a Telecom call during CarPlay calls so the car's own phone audio state, and its echo canceller,
     * handle the call. Replaces DiPlay's own canceller for those calls; applies at the next call.
     */
    fun phoneAudioRoute(context: Context): Boolean = prefs(context).getBoolean(KEY_PHONE_AUDIO_ROUTE, false)

    fun setPhoneAudioRoute(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_PHONE_AUDIO_ROUTE, enabled).apply()

    /** The same for voice-over-IP calls in other apps; only calls that record from VOICE_COMMUNICATION qualify. */
    fun phoneAudioRouteApps(context: Context): Boolean = prefs(context).getBoolean(KEY_PHONE_AUDIO_ROUTE_APPS, false)

    fun setPhoneAudioRouteApps(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_PHONE_AUDIO_ROUTE_APPS, enabled).apply()


    fun videoWhileParkedActive(context: Context): Boolean =
        videoWhileParked(context) && supportedInSelectedMode(context) { it.gearSupported }

    /**
     * Use addresses saved by the legacy head-unit probe. Existing installations with a saved probe
     * migrate to this mode; a fresh installation stays on the default DiLink 5.0 path.
     */
    fun legacyVehicleProbe(context: Context): Boolean {
        val settings = prefs(context)
        return if (settings.contains(KEY_LEGACY_VEHICLE_PROBE)) {
            settings.getBoolean(KEY_LEGACY_VEHICLE_PROBE, false)
        } else {
            BydVehicleFieldStore.load(context) != null
        }
    }

    fun setLegacyVehicleProbe(context: Context, enabled: Boolean) {
        check(prefs(context).edit().putBoolean(KEY_LEGACY_VEHICLE_PROBE, enabled).commit()) {
            "Could not persist BYD vehicle-data mode"
        }
    }


    /** At or below this charge the iPhone gets the low-range warning. */
    fun lowChargePercent(context: Context): Int = prefs(context).getInt(KEY_LOW_CHARGE_PERCENT, DEFAULT_LOW_CHARGE_PERCENT)

    fun setLowChargePercent(context: Context, percent: Int) =
        prefs(context).edit().putInt(KEY_LOW_CHARGE_PERCENT, percent).apply()


    /** Whether this is a BYD head unit, so settings can show the car-specific options. */
    fun available(context: Context): Boolean =
        installed(context, "com.byd.carsettings") ||
            installed(context, "com.byd.appmgr") ||
            installed(context, "com.byd.deviceinfo") ||
            installed(context, "com.byd.service") ||
            android.os.Build.FINGERPRINT.contains("BYD", ignoreCase = true) ||
            android.os.Build.BRAND.contains("BYD", ignoreCase = true) ||
            android.os.Build.MANUFACTURER.contains("BYD", ignoreCase = true) ||
            android.os.Build.PRODUCT.contains("BYD", ignoreCase = true) ||
            android.os.Build.DEVICE.contains("BYD", ignoreCase = true)

    private fun installed(context: Context, pkg: String): Boolean =
        runCatching { context.packageManager.getPackageInfo(pkg, 0) }.isSuccess

    private fun supportedInSelectedMode(
        context: Context,
        supported: (BydVehicleCapabilities) -> Boolean,
    ): Boolean = !legacyVehicleProbe(context) || BydVehicleFieldStore.load(context)?.let(supported) == true

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
