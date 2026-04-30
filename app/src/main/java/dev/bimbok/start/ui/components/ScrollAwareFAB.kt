package dev.bimbok.start.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ScrollAwareFAB(
    lazyListState: LazyListState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var previousIndex by remember { mutableIntStateOf(lazyListState.firstVisibleItemIndex) }
    var previousScrollOffset by remember { mutableIntStateOf(lazyListState.firstVisibleItemScrollOffset) }

    val isVisible by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex > previousIndex) {
                false
            } else if (lazyListState.firstVisibleItemIndex < previousIndex) {
                true
            } else {
                lazyListState.firstVisibleItemScrollOffset <= previousScrollOffset
            }.also {
                previousIndex = lazyListState.firstVisibleItemIndex
                previousScrollOffset = lazyListState.firstVisibleItemScrollOffset
            }
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task")
        }
    }
}
