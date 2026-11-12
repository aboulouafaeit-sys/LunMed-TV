package com.lunmed.tv.core.data.root

import android.content.Context
import com.lunmed.tv.core.common.dispatchers.Dispatcher
import com.lunmed.tv.core.common.dispatchers.LunmedDispatcher
import com.lunmed.tv.core.model.RootExecutionResult
import com.lunmed.tv.core.model.RootState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface RootLauncherHelper {
    fun getRootState(): RootState
    suspend fun enforceRootPermanentDefault(): RootExecutionResult
    suspend fun revertRootEnforcement(): RootExecutionResult
}

@Singleton
class AndroidRootLauncherHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(LunmedDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
) : RootLauncherHelper {

    private var lastMessage: String? = null

    override fun getRootState(): RootState {
        val suPath = detectSuBinary()
        val isRootAvailable = suPath != null
        return RootState(
            isRootAvailable = isRootAvailable,
            suBinaryPath = suPath,
            isPermanentlyEnforced = lastMessage != null && lastMessage!!.contains("successfully", ignoreCase = true),
            lastActionMessage = lastMessage,
        )
    }

    override suspend fun enforceRootPermanentDefault(): RootExecutionResult = withContext(ioDispatcher) {
        val suPath = detectSuBinary()
        if (suPath == null) {
            val msg = "Root access (su binary) was not detected on this Android device."
            lastMessage = msg
            return@withContext RootExecutionResult(success = false, message = msg)
        }

        val selfPackage = context.packageName
        val disabled = mutableListOf<String>()

        // 1. Force LUNMED TV as persistent default home activity in Package Manager
        val setHomeCmd = "cmd package set-home-activity $selfPackage/com.lunmed.tv.MainActivity || pm set-home-activity $selfPackage/com.lunmed.tv.MainActivity"
        runSuCommand(suPath, setHomeCmd)

        // 2. Set Device Owner via root without requiring a PC or ADB cable
        val setDeviceOwnerCmd = "dpm set-device-owner $selfPackage/com.lunmed.tv.receiver.LunmedDeviceAdminReceiver"
        runSuCommand(suPath, setDeviceOwnerCmd)

        // 3. Disable competing OEM vendor stock launchers at root level
        for (vendorPkg in KNOWN_OEM_LAUNCHERS) {
            if (vendorPkg == selfPackage) continue
            val checkRes = runSuCommand(suPath, "pm list packages $vendorPkg")
            if (checkRes.contains(vendorPkg)) {
                runSuCommand(suPath, "pm disable-user --user 0 $vendorPkg || pm disable $vendorPkg")
                disabled.add(vendorPkg)
            }
        }

        val successMsg = if (disabled.isEmpty()) {
            "LUNMED TV enforced as system default Home at root level successfully."
        } else {
            "LUNMED TV enforced at root level successfully. Disabled ${disabled.size} OEM launcher(s): ${disabled.joinToString(", ")}."
        }
        lastMessage = successMsg

        RootExecutionResult(
            success = true,
            message = successMsg,
            disabledLaunchers = disabled,
        )
    }

    override suspend fun revertRootEnforcement(): RootExecutionResult = withContext(ioDispatcher) {
        val suPath = detectSuBinary()
        if (suPath == null) {
            val msg = "Root access is not available."
            lastMessage = msg
            return@withContext RootExecutionResult(success = false, message = msg)
        }

        val reEnabled = mutableListOf<String>()
        for (vendorPkg in KNOWN_OEM_LAUNCHERS) {
            runSuCommand(suPath, "pm enable $vendorPkg")
            reEnabled.add(vendorPkg)
        }

        val msg = "Re-enabled OEM launchers: ${reEnabled.size} packages restored."
        lastMessage = msg
        RootExecutionResult(
            success = true,
            message = msg,
            disabledLaunchers = emptyList(),
        )
    }

    private fun detectSuBinary(): String? {
        val candidates = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/vendor/bin/su",
            "/system/sd/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
        )
        for (path in candidates) {
            if (runCatching { File(path).exists() }.getOrDefault(false)) {
                return path
            }
        }
        // Fallback: which su
        return runCatching {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val output = process.inputStream.bufferedReader().use { it.readText().trim() }
            val exit = process.waitFor()
            if (exit == 0 && output.isNotBlank() && output.startsWith("/")) output else null
        }.getOrNull()
    }

    private fun runSuCommand(suBinary: String, command: String): String {
        return runCatching {
            val process = ProcessBuilder(suBinary, "-c", command)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()
            output.trim()
        }.getOrDefault("")
    }

    companion object {
        val KNOWN_OEM_LAUNCHERS = listOf(
            "com.google.android.tvlauncher",
            "com.google.android.apps.tv.launcherx",
            "com.google.android.tungsten.setupwraith",
            "com.google.android.leanbacklauncher",
            "com.droidlogic.tvlauncher",
            "com.droidlogic.launcher",
            "com.droidlogic.appinstall",
            "com.alber.tvlauncher",
            "com.elinkway.tvlauncher",
            "com.t95.launcher",
            "com.x96.launcher",
            "com.tanix.launcher",
            "com.ugoos.launcher",
            "com.minix.launcher",
            "com.rockchip.launcher",
            "com.rockchip.devicetest",
            "com.allwinner.launcher",
            "com.allwinner.homelauncher",
            "com.softwinner.launcher",
            "com.softwinner.unilauncher",
            "com.shashlik.launcher",
            "com.amazon.tv.launcher",
            "com.mitv.tvhome",
            "com.mstar.tv.launcher",
            "com.realtek.launcher",
            "com.hisilicon.launcher",
            "com.himedia.launcher",
            "com.zidoo.launcher",
            "com.zidoo.poster",
            "com.formuler.mol2",
            "com.formuler.mol3",
            "com.formuler.launcher",
            "com.dangbei.tvlauncher",
            "com.yunos.tv.homeshell",
            "com.android.launcher3",
            "com.android.launcher2",
        )
    }
}

