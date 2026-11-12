package com.lunmed.tv.core.data.root

import android.content.ContextWrapper
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidRootLauncherHelperTest {

    private val dummyContext = object : ContextWrapper(null) {
        override fun getPackageName(): String = "com.lunmed.tv"
    }
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun getRootState_returnsSafeDefaultOnUnrootedEnvironment() {
        val helper = AndroidRootLauncherHelper(dummyContext, testDispatcher)
        val state = helper.getRootState()
        assertNotNull(state)
        assertFalse(state.isPermanentlyEnforced)
    }

    @Test
    fun enforceRootPermanentDefault_failsSafelyWhenRootMissing() = runTest(testDispatcher) {
        val helper = AndroidRootLauncherHelper(dummyContext, testDispatcher)
        val result = helper.enforceRootPermanentDefault()
        assertNotNull(result)
    }

    @Test
    fun revertRootEnforcement_failsSafelyWhenRootMissing() = runTest(testDispatcher) {
        val helper = AndroidRootLauncherHelper(dummyContext, testDispatcher)
        val result = helper.revertRootEnforcement()
        assertNotNull(result)
    }

    @Test
    fun knownOemLaunchersList_containsCriticalOemPackages() {
        assertTrue(AndroidRootLauncherHelper.KNOWN_OEM_LAUNCHERS.contains("com.droidlogic.tvlauncher"))
        assertTrue(AndroidRootLauncherHelper.KNOWN_OEM_LAUNCHERS.contains("com.rockchip.launcher"))
        assertTrue(AndroidRootLauncherHelper.KNOWN_OEM_LAUNCHERS.contains("com.allwinner.launcher"))
        assertTrue(AndroidRootLauncherHelper.KNOWN_OEM_LAUNCHERS.contains("com.amazon.tv.launcher"))
    }
}
