plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.example.incometracker"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.incometracker"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  buildFeatures { compose = true }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.14"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
}

dependencies {
  implementation(platform("androidx.compose:compose-bom:2024.06.00"))
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material-icons-extended")

  implementation("com.google.android.material:material:1.12.0")

  // Force AndroidX Core to compileSdk 34 compatible:
  implementation("androidx.core:core-ktx:1.13.1")

  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
  implementation("androidx.lifecycle:lifecycle-process:2.8.4")

  implementation("androidx.navigation:navigation-compose:2.7.7")

  implementation("androidx.room:room-runtime:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")

  implementation("androidx.datastore:datastore-preferences:1.1.1")
  implementation("androidx.work:work-runtime-ktx:2.9.1")

  implementation("androidx.biometric:biometric:1.1.0")
  implementation("androidx.security:security-crypto:1.1.0-alpha06")
  implementation("androidx.fragment:fragment-ktx:1.8.2")

  implementation("com.kizitonwose.calendar:compose:2.6.1")
}

configurations.configureEach {
  resolutionStrategy.force(
    // AndroidX core pinned:
    "androidx.core:core:1.13.1",
    "androidx.core:core-ktx:1.13.1",

    // Keep Kotlin 1.9 metadata everywhere:
    "org.jetbrains.kotlin:kotlin-stdlib:1.9.24",
    "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.24",
    "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.24",
    "org.jetbrains.kotlin:kotlin-stdlib-common:1.9.24",

    // Keep coroutines Kotlin-1.9 compatible:
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1",
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
  )
}
