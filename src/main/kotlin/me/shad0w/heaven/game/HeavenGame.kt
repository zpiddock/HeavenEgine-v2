package me.shad0w.heaven.game

import me.shad0w.heaven.engine.EngineManager
import me.shad0w.heaven.engine.WindowManager

fun main(args: Array<String>) {

    val game = HeavenGame();
    game.main()
}

class HeavenGame {

    private lateinit var windowManager: WindowManager;
    private lateinit var engineManager: EngineManager;

    fun main() {

        windowManager = WindowManager("HeavenGame", 640, 480, false)
        engineManager = EngineManager(windowManager)

        engineManager.start()
    }
}