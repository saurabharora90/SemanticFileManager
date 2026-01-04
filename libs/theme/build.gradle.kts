plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.foundry.base)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "dev.bongballe.libs.theme"
}

foundry {
  features {
    compose()
  }
}

dependencies {
  api(platform(libs.compose.bom))
  api(libs.compose.material3)
  api(libs.compose.ui)

  implementation(libs.androidx.core.ktx)
  implementation(libs.compose.ui.tooling.preview)
}
