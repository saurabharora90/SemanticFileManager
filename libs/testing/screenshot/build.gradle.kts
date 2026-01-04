plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.foundry.base)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "dev.bongballe.libs.screenshot"
}

foundry {
  features {
    compose()
  }
}

dependencies {
  api(platform(libs.compose.bom))
  api(libs.bundles.compose.test)
  api(libs.compose.runtime)
  api(libs.roborazzi)

  implementation(project(":libs:theme"))
  implementation(libs.androidx.core.ktx)
  implementation(libs.compose.foundation)
  implementation(libs.robolectric)
  implementation(libs.roborazzi.rule)
}
