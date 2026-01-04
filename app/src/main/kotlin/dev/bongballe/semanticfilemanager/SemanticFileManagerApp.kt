package dev.bongballe.semanticfilemanager

import android.app.Application
import dev.bongballe.semanticfilemanager.di.AppGraph
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class SemanticFileManagerApp :
  Application(),
  MetroApplication {

  private val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

  override val appComponentProviders: MetroAppComponentProviders
    get() = appGraph
}
