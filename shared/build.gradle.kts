import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.sqldelight.runtime)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.extensions.paging)
            implementation(libs.sqldelight.extensions.coroutines)
            implementation(libs.androidx.datastore.preferences.core)
            implementation(libs.kotlin.json.serialization)
            implementation(libs.kotlinx.coroutines.core)
            // Multiplatform logging library
            implementation(libs.kermit)
        }
        commonTest{
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
            }
            languageSettings.apply {
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
            implementation(libs.security.crypto)
            implementation(libs.core.ktx)

        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.junit)
                implementation(libs.sqlDelight.driver)
            }
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

android {
    namespace = "com.ajay.seenu.expensetracker"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
}

sqldelight {
    databases {
        create("ExpenseDatabase") {
            packageName.set("com.ajay.seenu.expensetracker")
        }
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
