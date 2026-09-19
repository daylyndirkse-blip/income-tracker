plugins {
  id("com.android.application") version "8.5.2" apply false
  id("org.jetbrains.kotlin.android") version "1.9.24" apply false
  id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
implementation("androidx.core:core-ktx:1.13.1")
}
configurations.configureEach {
  resolutionStrategy.force(
    "androidx.core:core:1.13.1",
    "androidx.core:core-ktx:1.13.1"
  )
}
