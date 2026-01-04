plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.foundry.base)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
}


android {
  namespace = "dev.bongballe.semanticfilemanager"

  defaultConfig {
    applicationId = "dev.bongballe.semanticfilemanager"
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }

    debug {
      applicationIdSuffix = ".dev"
    }
  }
}

foundry {
  features {
    compose()
    metro()
  }
}

dependencies {
  implementation(platform(libs.compose.bom))
  implementation(project(":features:browser"))
  implementation(project(":libs:base"))
  implementation(project(":libs:theme"))
  implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.bundles.compose)
  implementation(libs.coil.compose)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.serialization.core)
  implementation(libs.metrox.android)
  implementation(libs.metrox.viewmodel)
  implementation(libs.metrox.viewmodel.compose)

  debugImplementation(libs.bundles.compose.debug)

  testImplementation(libs.junit)
}
