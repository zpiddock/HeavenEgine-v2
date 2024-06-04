package me.shad0w.heaven.engine

import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil

class WindowManager(windowTitle: String, var width: Int, var height: Int, val vSync: Boolean) : Manager {

    var windowTitle = windowTitle
        get() = field
        set(value) {
            GLFW.glfwSetWindowTitle(windowId, value)
            field = value
        }
    val FOV = Math.toRadians(90.0).toFloat()
    val Z_NEAR = 0.01f
    val Z_FAR = 1000f

    private var windowId: Long = 0

    private var resized: Boolean = false

    val projectionMatrix = Matrix4f()

    override fun initialise() {

        glfwInitialise()
        openGlInitialise()
    }

    private fun glfwInitialise() {

        // Set our error callback so glfw output goes to the jvm error stream
        GLFWErrorCallback.createPrint(System.err).set()

        // Check if glfw can be initialised
        // if not, throw an error
        if(!GLFW.glfwInit())
            throw IllegalStateException("GLFW is unable to initialise :(")

        // Begin window creation
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3)
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE)
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE)

        var maximised = false
        if(width == 0 || height == 0) {
            width = 100
            height = 100
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE)
            maximised = true
        }

        windowId = GLFW.glfwCreateWindow(
            width,
            height,
            windowTitle,
            MemoryUtil.NULL,
            MemoryUtil.NULL
        )

        if(windowId == MemoryUtil.NULL)
            throw RuntimeException("GLFW was unable to create our window :(")

        GLFW.glfwSetFramebufferSizeCallback(windowId,
            (GLFWFramebufferSizeCallbackI { window, width, height ->
                println(String.format("w: $width, h: $height"))
                this.width = width
                this.height = height
                resized = true
            }))

        GLFW.glfwSetKeyCallback(windowId,
            (GLFWKeyCallbackI { window, key, scancode, action, mods ->

                if(key == GLFW.GLFW_KEY_ESCAPE &&
                    action == GLFW.GLFW_RELEASE)
                    GLFW.glfwSetWindowShouldClose(windowId, true)
            }))

        if(maximised)
            GLFW.glfwMaximizeWindow(windowId)
        else {
            val vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
            GLFW.glfwSetWindowPos(windowId,
                (vidMode!!.width() - width) / 2,
                (vidMode.height() - height) / 2)
        }

        GLFW.glfwMakeContextCurrent(windowId)
        if(vSync)
            GLFW.glfwSwapInterval(1)

        GLFW.glfwShowWindow(windowId)
    }

    private fun openGlInitialise() {

        GL.createCapabilities()
        GL11.glClearColor(0f, 0f, 0f, 0f)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_STENCIL_TEST)
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glCullFace(GL11.GL_BACK)
    }

    override fun update() {

        GLFW.glfwSwapBuffers(windowId)
        GLFW.glfwPollEvents()
    }

    override fun cleanup() {

        GLFW.glfwDestroyWindow(windowId)
    }

    fun setClearColour(r: Float, g: Float, b: Float, a: Float) {

        GL11.glClearColor(r, g, b, a)
    }

    fun isKeyPressed(keyCode: Int): Boolean {

        return GLFW.glfwGetKey(windowId, keyCode) == GLFW.GLFW_PRESS
    }

    fun windowShouldClose(): Boolean {

        return GLFW.glfwWindowShouldClose(windowId)
    }

    private fun updateProjectionMatrix(): Matrix4f {

        return updateProjectionMatrix(projectionMatrix, width, height)
    }

    private fun updateProjectionMatrix(matrix: Matrix4f, width: Int, height: Int): Matrix4f {

        val aspectRatio = (width / height).toFloat()
        return matrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR)
    }
}