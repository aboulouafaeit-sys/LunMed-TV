package com.lunmed.tv.core.navigation.remote

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

/**
 * Normalized remote-control commands across diverse TV remotes,
 * gamepads, IR blasters, and CEC controllers.
 */
enum class RemoteCommand {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    SELECT,
    BACK,
    MENU,
    HOME,
    SETTINGS,
}

object RemoteKeyMapper {
    fun mapKeyCode(keyCode: Int): RemoteCommand? = when (keyCode) {
        AndroidKeyEvent.KEYCODE_DPAD_UP -> RemoteCommand.MOVE_UP
        AndroidKeyEvent.KEYCODE_DPAD_DOWN -> RemoteCommand.MOVE_DOWN
        AndroidKeyEvent.KEYCODE_DPAD_LEFT -> RemoteCommand.MOVE_LEFT
        AndroidKeyEvent.KEYCODE_DPAD_RIGHT -> RemoteCommand.MOVE_RIGHT
        AndroidKeyEvent.KEYCODE_DPAD_CENTER,
        AndroidKeyEvent.KEYCODE_ENTER,
        AndroidKeyEvent.KEYCODE_NUMPAD_ENTER -> RemoteCommand.SELECT
        AndroidKeyEvent.KEYCODE_BACK,
        AndroidKeyEvent.KEYCODE_ESCAPE -> RemoteCommand.BACK
        AndroidKeyEvent.KEYCODE_MENU,
        AndroidKeyEvent.KEYCODE_BOOKMARK -> RemoteCommand.MENU
        AndroidKeyEvent.KEYCODE_HOME -> RemoteCommand.HOME
        AndroidKeyEvent.KEYCODE_SETTINGS -> RemoteCommand.SETTINGS
        else -> null
    }
}

/**
 * Intercepts key-down remote commands before child traversal when needed
 * (e.g., MENU key to open contextual actions for the focused item).
 */
fun Modifier.onRemoteKeyCommand(
    onCommand: (RemoteCommand) -> Boolean,
): Modifier = this.onPreviewKeyEvent { event ->
    if (event.type == KeyEventType.KeyDown) {
        val command = RemoteKeyMapper.mapKeyCode(event.nativeKeyEvent.keyCode)
        if (command != null) {
            onCommand(command)
        } else {
            false
        }
    } else {
        false
    }
}

