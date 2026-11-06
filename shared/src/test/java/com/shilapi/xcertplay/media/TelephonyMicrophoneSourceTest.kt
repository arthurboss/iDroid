package com.shilapi.xcertplay.media

import android.media.MediaRecorder
import org.junit.Assert.assertEquals
import org.junit.Test

class TelephonyMicrophoneSourceTest {
    private fun source(speakerphone: Boolean, carPhoneState: Boolean) =
        telephonyMicrophoneSource(speakerphone, carPhoneState)

    @Test fun theDevicesPhoneStateUsesVoiceRecognition() {
        assertEquals(MediaRecorder.AudioSource.VOICE_RECOGNITION, source(speakerphone = false, carPhoneState = true))
    }

    @Test fun thePhoneStateNeverUsesThePlainMicrophoneThatItMutes() {
        assertEquals(MediaRecorder.AudioSource.VOICE_RECOGNITION, source(speakerphone = true, carPhoneState = true))
    }

    @Test fun withoutThePhoneStateACallKeepsItsOldSources() {
        assertEquals(MediaRecorder.AudioSource.VOICE_COMMUNICATION, source(speakerphone = false, carPhoneState = false))
        assertEquals(MediaRecorder.AudioSource.MIC, source(speakerphone = true, carPhoneState = false))
    }
}
