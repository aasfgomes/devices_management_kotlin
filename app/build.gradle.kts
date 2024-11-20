plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
<<<<<<< HEAD

    id("com.google.gms.google-services")
    // id("com.android.application") -> cria uma erro de compilacão ao fazer sync do gradle
}

android {
    namespace = "com.computacaomovel.devicemanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.computacaomovel.devicemanagement"
        minSdk = 33
        targetSdk = 33
=======
}

android {
    namespace = "project.devices_management"
    compileSdk = 34

    defaultConfig {
        applicationId = "project.devices_management"
        minSdk = 33
        targetSdk = 34
>>>>>>> a530a6916f554091e7c98c97988f2f97b0dae3c4
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
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
<<<<<<< HEAD
    implementation(libs.firebase.firestore.ktx)
=======
>>>>>>> a530a6916f554091e7c98c97988f2f97b0dae3c4
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
<<<<<<< HEAD

    // Compose dependencies
    implementation ("androidx.compose.ui:ui:1.5.1")
    implementation ("androidx.compose.material3:material3:1.1.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    implementation(libs.firebase.bom)

=======
>>>>>>> a530a6916f554091e7c98c97988f2f97b0dae3c4
}