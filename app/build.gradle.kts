plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.planifyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.planifyapp"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core y Compose
    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.lifecycle.runtime.ktx.v262)
    implementation(libs.androidx.activity.compose.v182)
    implementation(platform(libs.androidx.compose.bom.v20240201))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    // Firebase
    implementation(platform(libs.firebase.bom.v3270))
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database)
    implementation(libs.google.firebase.analytics)

    // Google Play Services
    implementation(libs.play.services.maps)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20240201))
    androidTestImplementation(libs.ui.test.junit4)

    // Debug
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Navegación y ConstraintLayout
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.runtime.livedata)

    // Corrutinas
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // ViewModel + Corrutinas
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Coil para imágenes
    implementation(libs.coil.compose)

    // Material Icons Extended
    implementation(libs.androidx.material.icons.extended)

    // Cámara
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    // Accompanist para permisos
    implementation(libs.accompanist.permissions)

    // WorkManager para tareas programadas
    implementation(libs.androidx.work.runtime.ktx)
}