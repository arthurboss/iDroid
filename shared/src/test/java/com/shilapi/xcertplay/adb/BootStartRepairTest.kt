package com.shilapi.xcertplay.adb

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BydBootStartRepairTest {
    @Test fun whitelistRequiresTheRequestedPackageEntry() {
        val pkg = "com.shilapi.xcertplay"
        assertTrue(BootStartRepair.isWhitelisted("user,$pkg,10234\n", pkg))
        assertTrue(BootStartRepair.isWhitelisted("system,$pkg,10234", pkg))
        assertFalse(BootStartRepair.isWhitelisted("user,$pkg.debug,10234", pkg))
        assertFalse(BootStartRepair.isWhitelisted("Permission denied for $pkg", pkg))
        assertFalse(BootStartRepair.isWhitelisted(null, pkg))
    }
}
