package dev.bimbok.start.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.bimbok.start.data.local.models.AppTheme
import dev.bimbok.start.ui.settings.viewmodel.SettingsViewModel
import dev.bimbok.start.ui.theme.getDynamicGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentTheme by viewModel.theme.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    val themeSheetState = rememberModalBottomSheetState()

    // Backup & Restore Launchers
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            viewModel.backupData(context, it, 
                onSuccess = { Toast.makeText(context, "Backup saved", Toast.LENGTH_SHORT).show() },
                onError = { Toast.makeText(context, "Backup failed: $it", Toast.LENGTH_SHORT).show() }
            )
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.restoreData(context, it,
                onSuccess = { Toast.makeText(context, "Data restored", Toast.LENGTH_SHORT).show() },
                onError = { Toast.makeText(context, "Restore failed: $it", Toast.LENGTH_SHORT).show() }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("CLEAR ALL DATA", fontWeight = FontWeight.Black) },
            text = { Text("This will permanently delete all your tasks and tags. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showDeleteDialog = false
                        Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("DELETE EVERYTHING", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large
        )
    }

    if (showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            sheetState = themeSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("CHOOSE THEME", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                
                AppTheme.entries.forEach { theme ->
                    Surface(
                        onClick = { 
                            viewModel.setTheme(theme)
                            showThemeSheet = false
                        },
                        shape = MaterialTheme.shapes.medium,
                        color = if (currentTheme == theme) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                        border = if (currentTheme == theme) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = theme.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (currentTheme == theme) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTheme == theme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (currentTheme == theme) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SETTINGS",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 42.sp,
                            letterSpacing = 4.sp,
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(getDynamicGradient())
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SettingCategory("GENERAL") }
            item {
                SettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Manage task reminders and alerts",
                    onClick = {
                        Toast.makeText(context, "Notification settings coming in v1.1", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.Palette,
                    title = "Theme",
                    subtitle = currentTheme.label,
                    onClick = { showThemeSheet = true }
                )
            }

            item { SettingCategory("DATA") }
            item {
                SettingItem(
                    icon = Icons.Default.CloudUpload,
                    title = "Backup Data",
                    subtitle = "Export tasks to a JSON file",
                    onClick = {
                        createDocumentLauncher.launch("start_backup_${System.currentTimeMillis()}.json")
                    }
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.Download,
                    title = "Restore Data",
                    subtitle = "Import tasks from a JSON file",
                    onClick = {
                        openDocumentLauncher.launch(arrayOf("application/json"))
                    }
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Clear All Data",
                    subtitle = "Permanently delete all tasks",
                    onClick = { showDeleteDialog = true },
                    isDestructive = true
                )
            }

            item { SettingCategory("ABOUT") }
            item {
                SettingItem(
                    icon = Icons.Default.Info,
                    title = "App Version",
                    subtitle = "1.0.0 (Retro Edition)",
                    onClick = {}
                )
            }
            item {
                SettingItem(
                    icon = Icons.Default.Code,
                    title = "GitHub",
                    subtitle = "View source code & contribute",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://github.com/malevolent-shrine-hq/Start.git")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun SettingCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDestructive) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}
