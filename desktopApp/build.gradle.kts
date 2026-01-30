plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val appName = "RpiAttendance"
val appPackageName = "shub39.rpi_attendance.client"
val appVersionCode = 1
val appVersionName = "1.0.0"

kotlin {
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
    }
    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)
            implementation(projects.client)

            implementation(compose.desktop.currentOs)

            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.filekit.core)
        }
    }
}

compose.desktop {
    application {
        mainClass = "$appPackageName.MainKt"
    }
}