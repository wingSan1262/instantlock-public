plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.composeCompiler)
    id("kotlin-parcelize")
}

android {
    namespace = "com.risyan.quickshutdownphone"
    compileSdk = 35
    ndkVersion = "28.2.13676358"
    defaultConfig {
        applicationId = "com.risyan.quickshutdownphone"
        minSdk = 30  // Android 11 (R) - Required for AccessibilityService.takeScreenshot()
        targetSdk = 35
        versionCode = 10
        versionName = "0.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    splits {
        // Configures multiple APKs based on ABI (CPU architecture)
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true  // Also generate a universal APK containing all ABIs
        }
    }

    signingConfigs {
        create("release") {
            // TODO: Replace with your keystore path and credentials
            storeFile = file("wing_android_apk_keys")
            storePassword = "krakatau"
            keyAlias = "key0"
            keyPassword = "krakatau"
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
        }
        release {
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.tensorflow.lite)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.material)
    implementation(libs.mlkit.pose.detection)
    implementation(libs.mlkit.pose.detection.accurate)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.litert)
    implementation(libs.litertSupport)
    implementation(libs.litertMetadata)
    implementation(libs.litertApi)

    testImplementation(libs.junit)


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}