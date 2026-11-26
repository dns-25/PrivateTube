import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

// The release signing key lives outside the project. Its keystore.properties (storeFile, storePassword, keyAlias,
// keyPassword) is named by the PRIVATETUBE_SIGNING variable or by "privatetube.signing" in local.properties.
val signingPath: String? = System.getenv("PRIVATETUBE_SIGNING") ?: Properties().apply {
    rootProject.file("local.properties").takeIf { it.isFile }?.reader(Charsets.UTF_8)?.use { load(it) }
}.getProperty("privatetube.signing")

android {
    namespace = "com.privatetube.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.privatetube.app"
        // minSdk 24: Compose/Media3 require >= 23, Android TV 7.0+ covers nearly all deployed boxes.
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    androidResources {
        // Only the languages of the app: drops the other translations bundled in the libraries.
        localeFilters += listOf("en", "ru", "uk", "es", "pt", "fr", "de", "it", "pl", "nl", "cs", "ja", "zh")
    }

    signingConfigs {
        if (signingPath != null) {
            val settingsFile = File(signingPath)
            // A configured key that cannot be read is an error: the APK must never get another signature by accident.
            require(settingsFile.isFile) { "Signing settings not found: $signingPath" }
            val settings = Properties().apply { settingsFile.reader(Charsets.UTF_8).use { load(it) } }
            create("release") {
                storeFile = File(settingsFile.parentFile, settings.getProperty("storeFile"))
                storePassword = settings.getProperty("storePassword")
                keyAlias = settings.getProperty("keyAlias")
                keyPassword = settings.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (signingPath == null) {
                logger.warn("No signing key configured: the release APK is signed with the debug key of this computer.")
            }
            signingConfig = signingConfigs.getByName(if (signingPath != null) "release" else "debug")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    packaging {
        resources {
            excludes += setOf("META-INF/*.version", "META-INF/LICENSE*", "META-INF/AL2.0", "META-INF/LGPL2.1", "DebugProbesKt.bin")
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.10.01")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Compose + TV Material
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.tv:tv-material:1.0.1")

    // Network / parsing
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jsoup:jsoup:1.21.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Images
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // Local storage
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Playback
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.8.0")
    implementation("androidx.media3:media3-datasource-okhttp:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    implementation("androidx.media3:media3-session:1.8.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}
