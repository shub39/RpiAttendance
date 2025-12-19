plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

val appName = "RpiAttendance"
val appPackageName = "shub39.rpi_attendance.client"
val appVersionName = "0.0.01"
val appVersionCode = 1

kotlin {
    jvmToolchain(17)
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
    }

    jvm()
    android {
        namespace = appPackageName
        compileSdk {
            version = release(libs.versions.android.compileSdk.get().toInt())
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.core)

            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.rpc.krpc.client)
            implementation(libs.kotlinx.rpc.krpc.ktor.client)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(compose.preview)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

dependencies {
    "androidRuntimeClasspath"(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "shub39.rpi_attendance.client.Mainkt"
    }
}