package com.vzyn.ffpanel.core

import androidx.compose.runtime.Immutable

enum class Kind { TOGGLE, SLIDER, OPTION, COLOR, ACTION }

@Immutable
data class Spec(
    val key: String,
    val pt: String,
    val en: String,
    val group: String,
    val kind: Kind,
    val min: Float = 0f,
    val max: Float = 100f,
    val step: Float = 1f,
    val default: Float = 0f,
    val options: List<String> = emptyList(),
    val unit: String = ""
)

fun Spec.text(lang: String): String = if (lang == "en") en else pt

fun toggle(key: String, pt: String, en: String, group: String, on: Boolean = false) =
    Spec(key, pt, en, group, Kind.TOGGLE, default = if (on) 1f else 0f)

fun slider(key: String, pt: String, en: String, group: String, min: Float, max: Float, def: Float, unit: String = "") =
    Spec(key, pt, en, group, Kind.SLIDER, min, max, 1f, def, unit = unit)

fun option(key: String, pt: String, en: String, group: String, options: List<String>, defIndex: Int = 0) =
    Spec(key, pt, en, group, Kind.OPTION, 0f, (options.size - 1).toFloat(), 1f, defIndex.toFloat(), options)

fun color(key: String, pt: String, en: String, group: String, hue: Float = 195f) =
    Spec(key, pt, en, group, Kind.COLOR, 0f, 360f, 1f, hue)

fun action(key: String, pt: String, en: String, group: String) =
    Spec(key, pt, en, group, Kind.ACTION)

@Immutable
data class SpecGroup(
    val id: String,
    val titlePt: String,
    val titleEn: String,
    val icon: String,
    val specs: List<Spec>
)
