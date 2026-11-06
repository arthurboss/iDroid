package com.shilapi.xcertplay

import android.app.AppOpsManager
import android.content.Context
import android.os.Process

/**
 * The owner's standard Android Usage Access grant, which lets DiPlay see which app is in front.
 * Used to hide the floating map card while another app is on screen.
 */
internal object UsageAccess {
    fun granted(context: Context): Boolean = context.getSystemService(AppOpsManager::class.java)
        .checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName) ==
        AppOpsManager.MODE_ALLOWED
}
