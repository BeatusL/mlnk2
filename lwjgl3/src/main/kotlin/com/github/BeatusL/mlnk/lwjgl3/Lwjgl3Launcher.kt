@file:JvmName("Lwjgl3Launcher")

package com.github.BeatusL.mlnk.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.BeatusL.mlnk.game

/** Launches the desktop (LWJGL3) application. */
fun main() {
    Lwjgl3Application(game, Lwjgl3ApplicationConfiguration().apply {
        setTitle("MLNK")
        setWindowedMode(720, 1280)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
