plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// Автоматически создаем правильную структуру приложения перед сборкой
val prepareSourceFiles by tasks.registering {
    doLast {
        val appDir = file("app")
        val mainDir = file("app/src/main")
        val javaDir = file("app/src/main/java/com/mercury/configurator")
        
        javaDir.mkdirs()

        file("$appDir/build.gradle.kts").writeText("""
            plugins {
                id("com.android.application")
                id("org.jetbrains.kotlin.android")
            }
            android {
                namespace = "com.mercury.configurator"
                compileSdk = 34
                defaultConfig {
                    applicationId = "com.mercury.configurator"
                    minSdk = 26
                    targetSdk = 34
                    versionCode = 1
                    versionName = "1.0"
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_1_8
                    targetCompatibility = JavaVersion.VERSION_1_8
                }
                kotlinOptions { jvmTarget = "1.8" }
                buildFeatures { compose = true }
                composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }
            }
            dependencies {
                implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
                implementation("androidx.activity:activity-compose:1.8.2")
                implementation(platform("androidx.compose:compose-bom:2024.02.00"))
                implementation("androidx.compose.ui:ui")
                implementation("androidx.compose.ui:ui-graphics")
                implementation("androidx.compose.ui:ui-material3")
                implementation("androidx.compose.material3:material3")
                implementation("com.github.mik3y:usb-serial-for-android:3.7.0")
            }
        """.trimIndent())

        file("$mainDir/AndroidManifest.xml").writeText("""
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                <uses-permission android:name="android.permission.INTERNET" />
                <uses-feature android:name="android.hardware.usb.host" />
                <application
                    android:allowBackup="true"
                    android:label="Mercury Configurator"
                    android:theme="@android:style/Theme.Material.Light.NoActionBar">
                    <activity android:name=".MainActivity" android:exported="true">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent())

        file("$javaDir/MainActivity.kt").writeText("""
            package com.mercury.configurator

            import android.os.Bundle
            import androidx.activity.ComponentActivity
            import androidx.activity.compose.setContent
            import androidx.compose.foundation.layout.*
            import androidx.compose.material3.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            class MainActivity : ComponentActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContent {
                        MaterialTheme {
                            Surface(modifier = Modifier.fillMaxSize()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Mercury & SPODES Configurator", style = MaterialTheme.typography.headlineMedium)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = {}) {
                                        Text("Опросить прибор учета")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        """.trimIndent())
    }
}
