package dev.bimbok.start.ui.notes

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bimbok.start.data.local.entities.Note
import dev.bimbok.start.ui.notes.viewmodel.NotesUiState
import dev.bimbok.start.ui.notes.viewmodel.NotesViewModel
import dev.bimbok.start.ui.theme.getDynamicGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    var isViewMode by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }
    val sheetState = rememberModalBottomSheetState()
    
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("DELETE NOTE", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteToDelete?.let { viewModel.deleteNote(it) }
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("DELETE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("CANCEL")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .blur(if (showSheet) 20.dp else 0.dp) // Apply blur when sheet is open
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "NOTES",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 42.sp,
                            letterSpacing = 4.sp,
                            brush = Brush.linearGradient(getDynamicGradient())
                        )
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is NotesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NotesUiState.Success -> {
                    if (state.notes.isEmpty()) {
                        NotesEmptyState(onAddClick = {
                            editingNote = null
                            isViewMode = false
                            showSheet = true
                        })
                    } else {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalItemSpacing = 12.dp
                        ) {
                            items(state.notes, key = { it.id }) { note ->
                                NoteCard(
                                    note = note,
                                    onClick = {
                                        editingNote = note
                                        isViewMode = true
                                        showSheet = true
                                    },
                                    onLongClick = {
                                        noteToDelete = note
                                    }
                                )
                            }
                        }
                    }
                }
                is NotesUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
            }

            Surface(
                onClick = { 
                    editingNote = null
                    isViewMode = false
                    showSheet = true 
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 120.dp)
                    .size(64.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f)),
                shadowElevation = 12.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Note",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

    if (showSheet) {
        NoteBottomSheet(
            note = editingNote,
            isViewMode = isViewMode,
            onDismiss = { 
                showSheet = false
                editingNote = null
                isViewMode = false
            },
            onEditMode = { isViewMode = false },
            onSave = { title, content ->
                if (editingNote != null) {
                    viewModel.updateNote(editingNote!!.copy(title = title, content = content))
                } else {
                    viewModel.addNote(title, content)
                }
                showSheet = false
                editingNote = null
            },
            sheetState = sheetState
        )
    }
}

@Composable
fun NotesEmptyState(onAddClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .background(Color.Transparent)
            .clickable(
                onClick = onAddClick, 
                indication = null, 
                interactionSource = remember { MutableInteractionSource() }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "NO NOTES FOUND",
            modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale),
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                brush = Brush.linearGradient(getDynamicGradient())
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "TAP TO CAPTURE YOUR IDEAS",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (note.title.isNotEmpty()) {
                Text(
                    text = note.title.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBottomSheet(
    note: Note? = null,
    isViewMode: Boolean = false,
    onDismiss: () -> Unit,
    onEditMode: () -> Unit,
    onSave: (String, String) -> Unit,
    sheetState: SheetState
) {
    var title by remember(note) { mutableStateOf(note?.title ?: "") }
    var content by remember(note) { mutableStateOf(note?.content ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), // Match Todo blur look
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)) },
        scrimColor = Color.Black.copy(alpha = 0.6f),
    ) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (note == null) "NEW NOTE" else if (isViewMode) "VIEW NOTE" else "EDIT NOTE",
                        style = if (note == null || isViewMode) {
                            MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                brush = Brush.linearGradient(getDynamicGradient())
                            )
                        } else {
                            MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                    
                    if (isViewMode) {
                        IconButton(onClick = onEditMode) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                if (isViewMode) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 500.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (title.isNotEmpty()) {
                            Text(
                                text = title.uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("CLOSE", fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Your ideas...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 400.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Button(
                        onClick = { if (content.isNotBlank()) onSave(title, content) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = content.isNotBlank(),
                        shape = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(if (note == null) "SAVE" else "UPDATE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
