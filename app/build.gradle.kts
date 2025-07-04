plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtoolKsp)
    alias(libs.plugins.daggerHilt)
}

android {
    namespace = "com.example.rateswap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rateswap"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildFeatures {
        buildConfig = true
    }


    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://developers.paysera.com/tasks/api/\"")
        }
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
    implementation(libs.retrofit.client)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.okhttp.logging)
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.ksp.compiler)
    implementation(libs.dagger.hilt.navigation)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.preference.datastore)
    implementation(libs.androidx.google.fonts)

    testImplementation(libs.junit)
    testImplementation(libs.cashapp.turbine)
    testImplementation(libs.google.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)
    testImplementation(libs.androidx.test.arch.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}