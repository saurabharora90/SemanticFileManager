package dev.bongballe.libs.base.qualifier

import dev.bongballe.libs.base.DispatcherType
import dev.zacsweers.metro.Qualifier

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class WithDispatcherType(val value: DispatcherType)
