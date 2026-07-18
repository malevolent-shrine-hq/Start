package dev.bimbok.start.ui.tags.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bimbok.start.data.local.entities.Tag
import dev.bimbok.start.data.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TagsUiState {
    object Loading : TagsUiState
    data class Success(val tags: List<Tag>) : TagsUiState
    data class Error(val message: String) : TagsUiState
}

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    val uiState: StateFlow<TagsUiState> = repository.allTags
        .map { tags -> TagsUiState.Success(tags) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TagsUiState.Loading
        )

    fun addTag(name: String, color: Int) {
        viewModelScope.launch {
            repository.insertTag(Tag(name = name, color = color))
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            repository.deleteTag(tag)
        }
    }
}
