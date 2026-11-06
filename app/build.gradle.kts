plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.portfolio.pixelglow"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.portfolio.pixelglow"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation:1.12.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // 2. Coil - Modern Image Loading (100% 16KB Compatible)
    implementation("io.coil-kt:coil-compose:2.7.0")

    // 3. Lifecycle & ViewModel for Architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // 🌟 Google ML Kit Face Detection (100% Native & 16KB Page Size Compatible)
    implementation("com.google.mlkit:face-detection:16.1.7")


    // 🌟 Room Local SQLite Database Engine
    val roomVersion = "2.7.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // 🌟 Lottie Animations Library for Jetpack Compose (100% 16KB Page Size Compatible)
    implementation("com.airbnb.android:lottie-compose:6.7.1")
}