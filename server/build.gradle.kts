plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    listOf(
        linuxArm64(),
        linuxX64()
    ).forEach { linuxTarget ->
        linuxTarget.binaries.executable {
            entryPoint = "main"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.curl)
        }
    }
}