plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.rpc)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val appName = "RpiAttendance"
val appPackageName = "shub39.rpi_attendance.client"
val appVersionCode = 1002
val appVersionName = "1.0.02"

android {
    namespace = appPackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = appPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.compileSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName
    }

    buildTypes {
        release {
            resValue("string", "app_name", appName)
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "$appName Debug")
            versionNameSuffix = "-debug"
        }
    }

    buildFeatures {
        compose = true
        resValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    implementation(projects.core)
    implementation(projects.client)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.components.resources)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.filekit.core)
}