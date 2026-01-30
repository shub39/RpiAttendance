plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlinx.rpc)
}

room {
    schemaDirectory("$projectDir/schemas")
}

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

    linuxX64 {
        binaries.executable {
            entryPoint = "main"
        }
    }
    linuxArm64 {
        binaries.executable {
            entryPoint = "main"
            linkerOpts("/usr/lib/gcc/aarch64-linux-gnu/15.1.0/libgcc.a")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.curl)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.kotlinx.rpc.krpc.server)
            implementation(libs.kotlinx.rpc.krpc.ktor.server)
            implementation(libs.kotlinx.rpc.krpc.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
    }
}

dependencies {
    add("kspLinuxX64", libs.androidx.room.compiler)
    add("kspLinuxArm64", libs.androidx.room.compiler)
}