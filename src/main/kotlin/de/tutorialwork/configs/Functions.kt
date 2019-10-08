package de.tutorialwork.configs

import kotlin.reflect.KProperty0

fun <T> setValue(kProperty0: KProperty0<T>, value: T) {
    val values = config.values.toMutableMap()
    values[kProperty0.name] = value
    config = Config(values)
}