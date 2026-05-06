package dev.bimbok.start.ui.theme

import androidx.compose.ui.graphics.Color

// Retro Terminal / Gruvbox-inspired Palette
val RetroBackground = Color(0xFF1B1817) // Deep, muted brownish-black
val RetroSurface = Color(0xFF262322)    // Slightly lighter brown-black
val RetroIvory = Color(0xFFEBDBB2)      // Crisp ivory text
val RetroYellow = Color(0xFFFABD2F)     // Soft terminal yellow
val RetroAqua = Color(0xFF8EC07C)       // Faded terminal aqua
val RetroGreen = Color(0xFFB8BB26)      // Muted green
val RetroOrange = Color(0xFFFE8019)     // Retro orange

// Terminal-style Gradients
val GlossyGradient = listOf(RetroAqua, RetroGreen)
val GlossyGradientCyan = listOf(RetroAqua, Color(0xFF458588)) // Aqua to Deep Blue
val GlossyGradientSun = listOf(RetroYellow, RetroOrange)
