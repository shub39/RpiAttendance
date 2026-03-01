/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.rpc)
}

kotlin {
    compilerOptions { optIn.add("kotlin.time.ExperimentalTime") }
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
        compileSdk { version = release(libs.versions.android.compileSdk.get().toInt()) }
        minSdk { version = release(libs.versions.android.minSdk.get().toInt()) }
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
            implementation(libs.materialkolor)
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.lifecycle.runtime)

            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)
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
