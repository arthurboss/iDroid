package com.shilapi.xcertplay.orchestration

/** A negotiated alt stream alone does not mean that the driver can see a map on the dashboard. */
internal object DashboardMapEligibility {
    /** Apple's instrument-cluster display URLs, as the iPhone names them when it negotiates the alt stream. */
    private const val MAP_URL = "maps:/car/instrumentcluster/map"
    private const val INSTRUMENTS_URL = "maps:/car/instrumentcluster"

    fun permits(initialUrl: String?, physicalOutputVisible: Boolean, stream: Int, uiShown: Boolean, closed: Boolean): Boolean =
        !closed && physicalOutputVisible && stream > 0 && uiShown &&
            (initialUrl == MAP_URL || initialUrl == INSTRUMENTS_URL)
}
