package dev.bimbok.start.ui.theme

import androidx.compose.ui.graphics.Color

// --- GRUVBOX (Retro Terminal) ---
val GruvBackground = Color(0xFF1B1817)
val GruvSurface = Color(0xFF262322)
val GruvIvory = Color(0xFFEBDBB2)
val GruvYellow = Color(0xFFFABD2F)
val GruvAqua = Color(0xFF8EC07C)
val GruvGreen = Color(0xFFB8BB26)
val GruvOrange = Color(0xFFFE8019)

// --- CATPPUCCIN (Mocha) ---
val CatBackground = Color(0xFF1E1E2E)
val CatSurface = Color(0xFF313244)
val CatText = Color(0xFFCDD6F4)
val CatYellow = Color(0xFFF9E2AF)
val CatTeal = Color(0xFF94E2D5)
val CatGreen = Color(0xFFA6E3A1)
val CatPeach = Color(0xFFFAB387)

// --- ROSE PINE ---
val RoseBackground = Color(0xFF191724)
val RoseSurface = Color(0xFF1F1D2E)
val RoseText = Color(0xFFE0DEF4)
val RoseGold = Color(0xFFF6C177)
val RosePine = Color(0xFF31748F)
val RoseIris = Color(0xFFC4A7E7)
val RoseLove = Color(0xFFEB6F92)

// Mapped defaults (Keeping Gruvbox as base for logic)
val RetroBackground = GruvBackground
val RetroSurface = GruvSurface
val RetroIvory = GruvIvory
val RetroYellow = GruvYellow
val RetroAqua = GruvAqua
val RetroGreen = GruvGreen
val RetroOrange = GruvOrange

// Terminal-style Gradients (Dynamic now)
val GlossyGradient = listOf(RetroAqua, RetroGreen)
val GlossyGradientCyan = listOf(RetroAqua, Color(0xFF458588))
val GlossyGradientSun = listOf(RetroYellow, RetroOrange)
