package com.lunmed.tv.core.model

/**
 * Root / Superuser status and telemetry for rooted Android TV boxes.
 */
data class RootState(
    val isRootAvailable: Boolean = false,
    val suBinaryPath: String? = null,
    val isPermanentlyEnforced: Boolean = false,
    val lastActionMessage: String? = null,
)

/**
 * Result of a root-level launcher enforcement or reversion operation.
 */
data class RootExecutionResult(
    val success: Boolean,
    val message: String,
    val disabledLaunchers: List<String> = emptyList(),
)

