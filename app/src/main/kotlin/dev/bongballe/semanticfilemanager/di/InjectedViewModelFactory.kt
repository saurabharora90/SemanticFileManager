package dev.bongballe.semanticfilemanager.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ViewModelAssistedFactory
import kotlin.reflect.KClass

@Inject
@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class InjectedViewModelFactory(
  val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>,
  val assistedFactoryProviders: Map<KClass<out ViewModel>, Provider<ViewModelAssistedFactory>>,
  val manualAssistedFactoryProviders: Map<KClass<out ManualViewModelAssistedFactory>, Provider<ManualViewModelAssistedFactory>>,
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
    assistedFactoryProviders[modelClass]?.let { factory ->
      return factory().create(extras) as T
    }

    viewModelProviders[modelClass]?.let { provider ->
      return provider() as T
    }

    throw IllegalArgumentException("Unknown model class $modelClass")
  }

  fun <FactoryType : ManualViewModelAssistedFactory> createManuallyAssistedFactory(
    factoryClass: KClass<FactoryType>,
  ): Provider<FactoryType> {
    manualAssistedFactoryProviders[factoryClass]?.let { provider ->
      @Suppress("UNCHECKED_CAST")
      return provider as Provider<FactoryType>
    }
    error("No manual viewModel provider found for $factoryClass")
  }
}
