plugins {
    kotlin("jvm") version "2.0.0"
}

val lwjgl_version by project.properties
val joml_version by project.properties
val joml_primitives_version by project.properties
val lwjgl_natives = getNatives()

group = "me.shad0w.heaven"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjgl_version"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-bgfx")
    implementation("org.lwjgl", "lwjgl-freetype")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-harfbuzz")
    implementation("org.lwjgl", "lwjgl-nanovg")
    implementation("org.lwjgl", "lwjgl-nfd")
    implementation("org.lwjgl", "lwjgl-nuklear")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-par")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-tinyfd")
    implementation("org.lwjgl", "lwjgl-vulkan")

    implementation("org.joml", "joml-primitives", "$joml_primitives_version")
    implementation("org.joml", "joml", "$joml_version")

    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-bgfx", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-freetype", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-harfbuzz", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-nanovg", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-par", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjgl_natives)
    runtimeOnly("org.lwjgl", "lwjgl-tinyfd", classifier = lwjgl_natives)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}

fun getNatives(): String {

    return Pair(
        System.getProperty("os.name")!!,
        System.getProperty("os.arch")
    )
        .let {(name, arch) ->
            when {
                arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
                    if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                        "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
                    else if (arch.startsWith("ppc"))
                        "natives-linux-ppc64le"
                    else if (arch.startsWith("riscv"))
                        "natives-linux-riscv64"
                    else
                        "natives-linux"
                arrayOf("Windows").any { name.startsWith(it) }                ->
                    "natives-windows"
                else                                                                            ->
                    throw Error("Unrecognized or unsupported platform. Please set \"lwjgl_natives\" manually")
            }
        }
}