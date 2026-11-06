package com.shilapi.xcertplay.setup

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SetupGuideTest {
    @Test fun theGuideOpensByItselfOnlyOnAFirstLaunchWithNoChosenPhone() {
        assertTrue(SetupGuide.shouldOpenOnLaunch(seen = false, phoneChosen = false))
        assertFalse(SetupGuide.shouldOpenOnLaunch(seen = true, phoneChosen = false))
        assertFalse(SetupGuide.shouldOpenOnLaunch(seen = false, phoneChosen = true))
        assertFalse(SetupGuide.shouldOpenOnLaunch(seen = true, phoneChosen = true))
    }

    @Test fun theStepsRunFromTheConnectionChoiceToDone() {
        assertTrue(SetupGuide.STEP_CONNECTION < SetupGuide.STEP_IPHONE)
        assertTrue(SetupGuide.STEP_IPHONE < SetupGuide.STEP_DONE)
        assertTrue(SetupGuide.STEP_DONE == SetupGuide.STEP_COUNT - 1)
    }
}
