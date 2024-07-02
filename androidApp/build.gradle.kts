import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hiltPlugin)
    id("kotlin-kapt")
}

android {
    namespace = "com.ajay.seenu.expensetracker.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.ajay.seenu.expensetracker.android"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.material)
    implementation(libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Hilt dependencies
    implementation(libs.hilt)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    // Koin dependencies
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Compose Navigation dependencies
    implementation(libs.compose.navigation)

    // Constraint layout
    implementation(libs.compose.constraint.layout)

    // Kotlinx DateTime
    implementation(libs.kotlinx.datetime)

    // Compose bottom sheet
    implementation(libs.compose.bottomsheet)

    // Vico Charts
    implementation(libs.vico.compose)
}