plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.foundry.base)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "dev.bongballe.features.browser"
}

foundry {
  features {
    compose()
    metro()
  }
  android {
    features {
      snapshotTests()
    }
  }
}

dependencies {
  implementation(platform(libs.compose.bom))
  implementation(project(":libs:base"))
  implementation(project(":libs:theme"))
  implementation(libs.androidx.core.ktx)
  implementation(libs.bundles.compose)
  implementation(libs.bundles.compose.debug)
  implementation(libs.coil.compose)
  implementation(libs.kotlinx.coroutines.core)

  testImplementation(project(":libs:testing:screenshot"))
}
