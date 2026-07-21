package dev.bimbok.start.ui.notes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.bimbok.start.data.local.entities.Note
import dev.bimbok.start.data.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NotesUiState {
    object Loading : NotesUiState
    data class Success(val notes: List<Note>) : NotesUiState
    data class Error(val message: String) : NotesUiState
}

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    val uiState: StateFlow<NotesUiState> = repository.allNotes
        .map { notes -> NotesUiState.Success(notes) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotesUiState.Loading
        )

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            repository.insertNote(Note(title = title, content = content))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}
