package com.example.maptracker.domain.model

enum class PinColor(val hex: String, val label: String) {
    RED("#F44336", "Red"),
    ORANGE("#FF9800", "Orange"),
    YELLOW("#FFC107", "Yellow"),
    GREEN("#4CAF50", "Green"),
    TEAL("#009688", "Teal"),
    BLUE("#2196F3", "Blue"),
    PURPLE("#9C27B0", "Purple"),
    PINK("#E91E63", "Pink");

    companion object {
        val DEFAULT = RED
        fun fromHex(hex: String): PinColor = entries.firstOrNull { it.hex == hex } ?: DEFAULT
    }
}
