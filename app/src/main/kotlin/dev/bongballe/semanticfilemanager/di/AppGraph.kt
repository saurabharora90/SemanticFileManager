package dev.bongballe.semanticfilemanager.di

import android.app.Application
import android.content.Context
import dev.bongballe.libs.base.DispatcherType
import dev.bongballe.libs.base.qualifier.WithDispatcherType
import dev.bongballe.libs.base.qualifier.WithScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@DependencyGraph(AppScope::class)
interface AppGraph :
  MetroAppComponentProviders,
  ViewModelGraph {

  @Provides
  fun provideApplicationContext(application: Application): Context = application

  @Provides
  @WithScope(AppScope::class)
  fun providesApplicationCoroutineScope(
    @WithDispatcherType(DispatcherType.MAIN) dispatcher: CoroutineDispatcher,
  ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(@Provides application: Application): AppGraph
  }
}
