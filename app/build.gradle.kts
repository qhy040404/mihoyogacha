import java.nio.file.Paths

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.qhy040404.mihoyogacha"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.qhy040404.mihoyogacha"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    packaging.resources.excludes += setOf(
        "DebugProbesKt.bin",
        "META-INF/*.version"
    )
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

configurations.all {
    exclude("androidx.appcompat", "appcompat")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("dev.rikka.rikkax.appcompat:appcompat:1.6.1")
    implementation("dev.rikka.rikkax.material:material:2.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("com.github.zhaobozhen.libraries:utils:1.1.4")
}

tasks.matching {
    it.name.contains("optimizeReleaseRes")
}.configureEach {
    doLast {
        val aapt2 = File(
            androidComponents.sdkComponents.sdkDirectory.get().asFile,
            "build-tools/${project.android.buildToolsVersion}/aapt2"
        )
        val zip = Paths.get(
            buildDir.path,
            "intermediates",
            "optimized_processed_res",
            "release",
            "resources-release-optimize.ap_"
        )
        val optimized = File("$zip.opt")
        val cmd = exec {
            commandLine(
                aapt2, "optimize",
                "--collapse-resource-names",
                "--resources-config-path", "aapt2-resources.cfg",
                "-o", optimized,
                zip
            )
            isIgnoreExitValue = false
        }
        if (cmd.exitValue == 0) {
            delete(zip)
            optimized.renameTo(zip.toFile())
        }
    }
}