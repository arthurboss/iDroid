package com.shilapi.xcertplay

import android.content.Context
import com.shilapi.xcertplay.network.CarHotspotSettings
import com.shilapi.xcertplay.orchestration.ManualHotspotValidation

/** When opening the app should bring the device's own hotspot up for the iPhone to join. */
internal object CarHotspotSetup {
    fun shouldStartOnLaunch(context: Context, hasSession: Boolean): Boolean =
        !hasSession && CarHotspotSettings.shouldEnable(context,
            AirPlayPersistence.loadWirelessEnabled(context), AirPlayPersistence.loadWirelessHotspotMode(context)) &&
            ManualHotspotValidation.error(AirPlayPersistence.loadManualHotspotSsid(context),
                AirPlayPersistence.loadManualHotspotPassphrase(context)) == null
}
