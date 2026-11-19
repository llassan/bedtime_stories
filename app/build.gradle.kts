import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Real AdMob ids live in ads.properties at the repo root (gitignored):
//   admobAppId=ca-app-pub-XXXXXXXX~YYYYYYYY
//   admobBannerUnitId=ca-app-pub-XXXXXXXX/ZZZZZZZZ
// Without that file every build uses Google's public test ids, which never earn or risk anything.
val adsProps = Properties().apply {
    val f = rootProject.file("ads.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val testAppId = "ca-app-pub-3940256099942544~3347511713"
val testBannerId = "ca-app-pub-3940256099942544/9214589741"

android {
    namespace = "com.vikash.bedtimestories"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vikash.bedtimestories"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "2.0.0"
        // The neural voice is too slow to be useful on 32-bit devices.
        ndk { abiFilters += "arm64-v8a" }
        manifestPlaceholders["admobAppId"] = testAppId
        buildConfigField("String", "ADMOB_BANNER_UNIT_ID", "\"$testBannerId\"")
    }

    buildTypes {
        release {
            // Debug builds always show test ads; only a release build uses the real ids.
            manifestPlaceholders["admobAppId"] = adsProps.getProperty("admobAppId") ?: testAppId
            buildConfigField("String", "ADMOB_BANNER_UNIT_ID", "\"${adsProps.getProperty("admobBannerUnitId") ?: testBannerId}\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    androidResources {
        // The voice model is memory-mapped straight from the APK.
        noCompress += listOf("onnx", "bin")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Offline neural text-to-speech (see scripts/fetch-voice.sh).
    implementation(files("libs/sherpa-onnx-1.13.8.aar"))

    implementation(libs.play.services.ads)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons)
}
