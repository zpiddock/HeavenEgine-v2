package me.shad0w.heaven.engine

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback

class EngineManager(private val windowManager: WindowManager) : Manager {

    private val NANOSECOND = 1000000000L
    private val targetFramerate = 1000
    private var currentFps: Int = 0
    private var frameTime = 1.0f / targetFramerate

    private var isRunning = false
    lateinit var errorCallback: GLFWErrorCallback

    override fun initialise() {

        errorCallback = GLFWErrorCallback.createPrint(System.err)
        GLFW.glfwSetErrorCallback(errorCallback)

        windowManager.initialise()
    }

    /**
     * Start
     *
     * @throws Exception
     */
    fun start() {

        initialise()

        if(isRunning)
            return
        run()
    }

    private fun run() {

        this.isRunning = true

        var frames = 0
        var frameCounter = 0L
        var lastTime = System.nanoTime()
        var unprocessedTime = 0.0

        while(isRunning) {

            var render = false
            var startTime = System.nanoTime()
            var passedTime = startTime - lastTime
            lastTime = startTime

            unprocessedTime += passedTime / NANOSECOND.toDouble()
            frameCounter += passedTime

            input()

            while(unprocessedTime > frameTime) {

                render = true
                unprocessedTime -= frameTime

                if(windowManager.windowShouldClose()) {
                    stop()
                }

                if(frameCounter >= NANOSECOND) {

                    currentFps = frames
                    windowManager.windowTitle = String.format("HeavenGame, FPS: $currentFps")
                    frames = 0
                    frameCounter = 0
                }
            }

            if (render) {
                update()
                render()
                frames++
            }
        }
        cleanup()
    }

    private fun stop() {

        if(!isRunning)
            return
        isRunning = false
    }

    private fun input() {

    }

    private fun render() {

    }

    override fun update() {

        windowManager.update()
    }

    override fun cleanup() {

        windowManager.cleanup()
        errorCallback.free()
        GLFW.glfwTerminate()
    }
}