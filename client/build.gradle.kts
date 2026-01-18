plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvmToolchain(17)
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
    androidLibrary {
        namespace = "shub39.rpi_attendance"
        compileSdk {
            version = release(libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
        androidResources.enable = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)

            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.rpc.krpc.client)
            implementation(libs.kotlinx.rpc.krpc.ktor.client)
            implementation(libs.kotlinx.rpc.krpc.serialization.json)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.datetime)
            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.compose.ui.tooling)
        }
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}