package com.lunmed.tv.service

import com.lunmed.tv.receiver.LunmedBootReceiver
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LunmedHomeWatchdogServiceTest {

    @Test
    fun isKnownVendorStockLauncher_identifiesAmlogicLaunchers() {
        assertTrue(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.droidlogic.tvlauncher"))
        assertTrue(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.droidlogic.launcher"))
    }

    @Test
    fun isKnownVendorStockLauncher_identifiesRockchipAndAllwinnerLaunchers() {
        assertTrue(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.rockchip.launcher"))
        assertTrue(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.allwinner.launcher"))
    }

    @Test
    fun isKnownVendorStockLauncher_identifiesGoogleAndFireTvLaunchers() {
        assertTrue(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.google.android.tvlauncher"))
        assertTrue(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.amazon.tv.launcher"))
        assertTrue(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.mitv.tvhome"))
    }

    @Test
    fun isKnownVendorStockLauncher_doesNotFlagNormalUserApps() {
        assertFalse(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.netflix.ninja"))
        assertFalse(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.google.android.youtube.tv"))
        assertFalse(LunmedHomeWatchdogService.isKnownVendorStockLauncher("com.lunmed.tv"))
    }

    @Test
    fun bootReceiver_handlesStandardAndDirectBootActions() {
        assertTrue(LunmedBootReceiver.BOOT_ACTIONS.contains("android.intent.action.BOOT_COMPLETED"))
        assertTrue(LunmedBootReceiver.BOOT_ACTIONS.contains("android.intent.action.LOCKED_BOOT_COMPLETED"))
        assertTrue(LunmedBootReceiver.BOOT_ACTIONS.contains("android.intent.action.QUICKBOOT_POWERON"))
        assertTrue(LunmedBootReceiver.BOOT_ACTIONS.contains("android.intent.action.REBOOT"))
    }
}

