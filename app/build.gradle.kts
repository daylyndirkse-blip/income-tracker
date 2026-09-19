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
  // Compose
  implementation(platform("androidx.compose:compose-bom:2024.06.00"))
  implementation("androidx.activity:activity-compose:1.9.2")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material-icons-extended")

  // Needed for Theme.Material3.* XML styles
  implementation("com.google.android.material:material:1.12.0")

  // Lifecycle/ViewModel
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
  implementation("androidx.lifecycle:lifecycle-process:2.8.4")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.7.7")

  // Room
  implementation("androidx.room:room-runtime:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  ksp("androidx.room:room-compiler:2.6.1")

  // DataStore
  implementation("androidx.datastore:datastore-preferences:1.1.1")

  // WorkManager
  implementation("androidx.work:work-runtime-ktx:2.9.1")

  // Biometric + security crypto
  implementation("androidx.biometric:biometric:1.1.0")
  implementation("androidx.security:security-crypto:1.1.0-alpha06")
  implementation("androidx.fragment:fragment-ktx:1.8.2")

  // Calendar (Compose)
  implementation("com.kizitonwose.calendar:compose:2.6.1")

  // Charts (Vico)
  implementation("com.patrykandpatrick.vico:compose:2.1.2")
  implementation("com.patrykandpatrick.vico:compose-m3:2.1.2")
}
