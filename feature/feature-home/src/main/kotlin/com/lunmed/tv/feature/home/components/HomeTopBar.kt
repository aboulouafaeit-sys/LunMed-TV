package com.lunmed.tv.feature.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lunmed.tv.core.model.DeviceCapabilities
import com.lunmed.tv.core.model.NetworkStatus
import com.lunmed.tv.core.navigation.LunmedDestination
import com.lunmed.tv.core.ui.components.LunmedTopBar

typealias HomeNavDestination = LunmedDestination

@Composable
fun HomeTopBar(
    activeDestination: HomeNavDestination,
    networkStatus: NetworkStatus,
    capabilities: DeviceCapabilities?,
    showClock: Boolean,
    showDeviceIndicators: Boolean,
    isDefaultLauncher: Boolean,
    canRequestDefaultLauncher: Boolean,
    onNavigate: (HomeNavDestination) -> Unit,
    onRequestDefaultLauncher: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LunmedTopBar(
        activeDestination = activeDestination,
        networkStatus = networkStatus,
        capabilities = capabilities,
        showClock = showClock,
        showDeviceIndicators = showDeviceIndicators,
        isDefaultLauncher = isDefaultLauncher,
        canRequestDefaultLauncher = canRequestDefaultLauncher,
        onNavigate = onNavigate,
        onRequestDefaultLauncher = onRequestDefaultLauncher,
        modifier = modifier,
    )
}
