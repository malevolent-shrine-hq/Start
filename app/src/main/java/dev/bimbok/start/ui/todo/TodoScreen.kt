package dev.bimbok.start.ui.todo

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bimbok.start.data.local.entities.Task
import dev.bimbok.start.ui.components.FloatingNavigationBar
import dev.bimbok.start.ui.components.ShimmerItem
import dev.bimbok.start.ui.components.TaskItem
import dev.bimbok.start.ui.theme.GlossyGradient
import dev.bimbok.start.ui.theme.GlossyGradientCyan
import dev.bimbok.start.ui.theme.LogoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val isBarVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 || listState.firstVisibleItemScrollOffset < 100
        }
    }

    // Background Animation
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
            // Further Dimmed & Animated Background Blobs with Higher Blur
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

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (showBottomSheet) 20.dp else 0.dp) // Blur content when sheet is open
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                containerColor = Color.Transparent,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { 
                            Text(
                                "Start",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontFamily = LogoFontFamily,
                                    fontSize = 52.sp, // Made it noticeably bigger
                                    brush = Brush.linearGradient(GlossyGradient)
                                )
                            ) 
                        },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        ),
                        windowInsets = WindowInsets.statusBars
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (val state = uiState) {
                        is TodoUiState.Loading -> {
                            LazyColumn(
                                contentPadding = PaddingValues(
                                    start = 20.dp, 
                                    end = 20.dp, 
                                    top = 16.dp, 
                                    bottom = 180.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(10) { ShimmerItem() }
                            }
                        }
                        is TodoUiState.Success -> {
                            if (state.tasks.isEmpty()) {
                                EmptyState()
                            } else {
                                LazyColumn(
                                    state = listState,
                                    contentPadding = PaddingValues(
                                        start = 20.dp, 
                                        end = 20.dp, 
                                        top = 16.dp, 
                                        bottom = 180.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(state.tasks, key = { it.task.id }) { taskWithTags ->
                                        TaskItem(
                                            taskWithSubtasksAndTags = taskWithTags,
                                            onToggleCompletion = { viewModel.toggleTaskCompletion(taskWithTags.task, it) },
                                            onDelete = { viewModel.deleteTask(taskWithTags.task) },
                                            onEdit = { 
                                                editingTask = taskWithTags.task
                                                showBottomSheet = true
                                            },
                                            onClick = { /* Detail */ }
                                        )
                                    }
                                }
                            }
                        }
                        is TodoUiState.Error -> {
                            Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    // Custom Glossy FAB
                    Surface(
                        onClick = { 
                            editingTask = null
                            showBottomSheet = true 
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 24.dp, bottom = 120.dp)
                            .size(64.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        shadowElevation = 12.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(GlossyGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add, 
                                contentDescription = "Add Task",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    FloatingNavigationBar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        isVisible = isBarVisible
                    )
                }
            }

            if (showBottomSheet) {
                TaskBottomSheet(
                    task = editingTask,
                    onDismiss = { 
                        showBottomSheet = false
                        editingTask = null
                    },
                    onConfirm = { title, desc ->
                        val currentTask = editingTask
                        if (currentTask != null) {
                            viewModel.updateTask(currentTask, title, desc)
                        } else {
                            viewModel.addTask(title, desc)
                        }
                        showBottomSheet = false
                        editingTask = null
                    },
                    sheetState = sheetState
                )
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Nothing started yet.",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = LogoFontFamily,
                fontSize = 46.sp,
                brush = Brush.linearGradient(GlossyGradient)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Tap + to begin your journey.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    task: Task? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    sheetState: SheetState
) {
    var title by remember(task) { mutableStateOf(task?.title ?: "") }
    var description by remember(task) { mutableStateOf(task?.description ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), // Slightly more opaque for prominence
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) },
        scrimColor = Color.Black.copy(alpha = 0.4f), // Darker scrim to focus on the window
    ) {
        // Content wrapped in a glassy surface with its own border
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    if (task == null) "Start New" else "Edit Task",
                    style = if (task == null) {
                        MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = LogoFontFamily,
                            fontSize = 38.sp,
                            brush = Brush.linearGradient(GlossyGradient)
                        )
                    } else {
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                )

                Button(
                    onClick = { if (title.isNotBlank()) onConfirm(title, description) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        if (task == null) "Start" else "Save",
                        style = if (task == null) {
                            MaterialTheme.typography.titleLarge.copy(
                                fontFamily = LogoFontFamily,
                                color = Color.White
                            )
                        } else {
                            MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
