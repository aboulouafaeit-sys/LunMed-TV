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
        AndroidKeyEvent.KEYCODE_DPAD_UP,
        AndroidKeyEvent.KEYCODE_PAGE_UP,
        AndroidKeyEvent.KEYCODE_CHANNEL_UP -> RemoteCommand.MOVE_UP

        AndroidKeyEvent.KEYCODE_DPAD_DOWN,
        AndroidKeyEvent.KEYCODE_PAGE_DOWN,
        AndroidKeyEvent.KEYCODE_CHANNEL_DOWN -> RemoteCommand.MOVE_DOWN

        AndroidKeyEvent.KEYCODE_DPAD_LEFT -> RemoteCommand.MOVE_LEFT
        AndroidKeyEvent.KEYCODE_DPAD_RIGHT -> RemoteCommand.MOVE_RIGHT

        AndroidKeyEvent.KEYCODE_DPAD_CENTER,
        AndroidKeyEvent.KEYCODE_ENTER,
        AndroidKeyEvent.KEYCODE_NUMPAD_ENTER,
        AndroidKeyEvent.KEYCODE_BUTTON_A,
        AndroidKeyEvent.KEYCODE_BUTTON_SELECT,
        AndroidKeyEvent.KEYCODE_BUTTON_START -> RemoteCommand.SELECT

        AndroidKeyEvent.KEYCODE_BACK,
        AndroidKeyEvent.KEYCODE_ESCAPE,
        AndroidKeyEvent.KEYCODE_BUTTON_B -> RemoteCommand.BACK

        AndroidKeyEvent.KEYCODE_MENU,
        AndroidKeyEvent.KEYCODE_BOOKMARK,
        AndroidKeyEvent.KEYCODE_INFO,
        AndroidKeyEvent.KEYCODE_GUIDE,
        AndroidKeyEvent.KEYCODE_WINDOW,
        AndroidKeyEvent.KEYCODE_BUTTON_X,
        AndroidKeyEvent.KEYCODE_BUTTON_Y -> RemoteCommand.MENU

        AndroidKeyEvent.KEYCODE_HOME -> RemoteCommand.HOME
        AndroidKeyEvent.KEYCODE_SETTINGS -> RemoteCommand.SETTINGS
        else -> null
    }
}

/**
 * Intercepts key-down remote commands before child traversal when needed
 * (e.g., MENU key to open contextual actions for the focused item).
 * Includes fast-repeat debounce protection for single-action commands.
 */
fun Modifier.onRemoteKeyCommand(
    onCommand: (RemoteCommand) -> Boolean,
): Modifier = this.onPreviewKeyEvent { event ->
    if (event.type == KeyEventType.KeyDown) {
        val nativeEvent = event.nativeKeyEvent
        val command = RemoteKeyMapper.mapKeyCode(nativeEvent.keyCode) ?: return@onPreviewKeyEvent false

        // Suppress repeating key events for action commands (MENU, SETTINGS)
        if (nativeEvent.repeatCount > 0 && (command == RemoteCommand.MENU || command == RemoteCommand.SETTINGS)) {
            return@onPreviewKeyEvent true
        }

        onCommand(command)
    } else {
        false
    }
}

