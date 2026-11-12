package com.lunmed.tv.core.navigation.remote

import android.view.KeyEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteKeyMapperTest {

    @Test
    fun `maps all standard TV remote dpad and action keys`() {
        assertEquals(RemoteCommand.MOVE_UP, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_DPAD_UP))
        assertEquals(RemoteCommand.MOVE_DOWN, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_DPAD_DOWN))
        assertEquals(RemoteCommand.MOVE_LEFT, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_DPAD_LEFT))
        assertEquals(RemoteCommand.MOVE_RIGHT, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT))

        assertEquals(RemoteCommand.SELECT, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_DPAD_CENTER))
        assertEquals(RemoteCommand.SELECT, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_ENTER))
        assertEquals(RemoteCommand.SELECT, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_NUMPAD_ENTER))
        assertEquals(RemoteCommand.SELECT, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_BUTTON_A))
        assertEquals(RemoteCommand.SELECT, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_BUTTON_SELECT))

        assertEquals(RemoteCommand.BACK, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_BACK))
        assertEquals(RemoteCommand.BACK, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_ESCAPE))
        assertEquals(RemoteCommand.BACK, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_BUTTON_B))

        assertEquals(RemoteCommand.MENU, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_MENU))
        assertEquals(RemoteCommand.MENU, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_INFO))
        assertEquals(RemoteCommand.MENU, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_GUIDE))
        assertEquals(RemoteCommand.MENU, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_BUTTON_X))

        assertEquals(RemoteCommand.MOVE_UP, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_PAGE_UP))
        assertEquals(RemoteCommand.MOVE_UP, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_CHANNEL_UP))
        assertEquals(RemoteCommand.MOVE_DOWN, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_PAGE_DOWN))
        assertEquals(RemoteCommand.MOVE_DOWN, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_CHANNEL_DOWN))

        assertEquals(RemoteCommand.HOME, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_HOME))
        assertEquals(RemoteCommand.SETTINGS, RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_SETTINGS))
    }

    @Test
    fun `unmapped volume and media keys return null to allow system pass-through`() {
        assertNull(RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_VOLUME_UP))
        assertNull(RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN))
        assertNull(RemoteKeyMapper.mapKeyCode(KeyEvent.KEYCODE_MUTE))
    }
}

