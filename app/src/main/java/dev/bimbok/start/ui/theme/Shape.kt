package dev.bimbok.start.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp), // Deeper corners for cards
    large = RoundedCornerShape(32.dp),
    extraLarge = RoundedCornerShape(40.dp) // Ultra rounded for bottom sheets/dialogs
)
