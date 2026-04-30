package dev.bimbok.start.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.bimbok.start.ui.components.FloatingNavigationBar
import dev.bimbok.start.ui.navigation.Screen
import dev.bimbok.start.ui.settings.SettingsScreen
import dev.bimbok.start.ui.tags.TagsScreen
import dev.bimbok.start.ui.theme.GlossyGradient
import dev.bimbok.start.ui.theme.GlossyGradientCyan
import dev.bimbok.start.ui.todo.TodoScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Background Animation (Global)
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "xOffset"
    )
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yOffset"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Global Decorative Background Blobs
            Canvas(modifier = Modifier.fillMaxSize().blur(150.dp)) {
                val dimmedGradient = GlossyGradient.map { it.copy(alpha = 0.1f) }
                val dimmedGradientCyan = GlossyGradientCyan.map { it.copy(alpha = 0.1f) }

                drawCircle(
                    brush = Brush.radialGradient(dimmedGradient),
                    radius = size.width * 1.5f,
                    center = center.copy(
                        x = size.width * (0.2f + 0.15f * xOffset),
                        y = size.height * (0.2f + 0.15f * yOffset)
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(dimmedGradientCyan),
                    radius = size.width * 1.2f,
                    center = center.copy(
                        x = size.width * (0.8f - 0.15f * xOffset),
                        y = size.height * (0.7f - 0.15f * yOffset)
                    )
                )
            }

            NavHost(
                navController = navController,
                startDestination = Screen.Tasks.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Tasks.route) {
                    TodoScreen()
                }
                composable(Screen.Tags.route) {
                    TagsScreen()
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }

            FloatingNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
