package com.shilapi.xcertplay.airplay

/**
 * CarPlay's secondary screen (stream type 111), used here as an off-screen map source.
 *
 * The iPhone lists the contents it offers for this screen in its /info reply (`altScreenURLs`);
 * without an initial URL it streams a black frame. The screen takes no input. The stream is not
 * shown on its own: copies of it feed the floating map card, the launcher map widget and the
 * map embed service, so its size is a fixed 16:9 canvas rather than any panel's size.
 */
object CarPlayAltScreen {
    const val MAP_URL = "maps:/car/instrumentcluster/map"

    const val STREAM_WIDTH = 1280
    const val STREAM_HEIGHT = 720
    private const val FPS = 30

    /**
     * Apple Maps keeps the car marker inside the safe area while the map still fills the canvas.
     * Balanced margins put the marker at the centre of a stream nothing crops.
     */
    val SAFE_AREA_PERCENT = AirPlayInsets(top = 8, bottom = 8, left = 8, right = 8)

    fun config(
        widthPixels: Int = STREAM_WIDTH,
        heightPixels: Int = STREAM_HEIGHT,
        url: String = MAP_URL,
    ): AirPlayDisplayConfig {
        val width = roundTo8(widthPixels.toDouble())
        val height = roundTo8(heightPixels.toDouble())
        return AirPlayDisplayConfig(
            widthPixels = width,
            heightPixels = height,
            fps = FPS,
            primaryInputDevice = 0,
            features = 0,
            initialUrl = url,
            safeArea = AirPlayInsets(
                top = height * SAFE_AREA_PERCENT.top / 100,
                bottom = height * SAFE_AREA_PERCENT.bottom / 100,
                left = width * SAFE_AREA_PERCENT.left / 100,
                right = width * SAFE_AREA_PERCENT.right / 100,
            ),
            safeAreaDrawOutside = true,
        )
    }

    // H.264 wants both dimensions on a multiple of 8; the encoder pads otherwise.
    private fun roundTo8(value: Double): Int = (Math.round(value / 8) * 8).toInt().coerceAtLeast(8)
}
