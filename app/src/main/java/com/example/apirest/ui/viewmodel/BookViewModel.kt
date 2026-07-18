package com.example.apirest.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apirest.data.model.Book
import com.example.apirest.data.repository.BookRepository
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class BookViewModel(private val repository: BookRepository) : ViewModel() {

    private val _booksState = mutableStateOf<UiState<List<Book>>>(UiState.Loading)
    val booksState: State<UiState<List<Book>>> = _booksState

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private var allBooks: List<Book> = emptyList()

    init {
        fetchBooks()
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        filterBooks(newQuery)
    }

    private fun filterBooks(query: String) {
        if (query.isBlank()) {
            _booksState.value = UiState.Success(allBooks)
        } else {
            val filtered = allBooks.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.author.contains(query, ignoreCase = true)
            }
            _booksState.value = UiState.Success(filtered)
        }
    }

    fun fetchBooks() {
        viewModelScope.launch {
            _booksState.value = UiState.Loading
            try {
                val books = repository.getBooks()
                allBooks = books
                filterBooks(_searchQuery.value)
            } catch (e: Exception) {
                _booksState.value = UiState.Error("Error al conectar con el servidor")
            }
        }
    }

    fun addBook(book: Book, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.addBook(book)
                fetchBooks()
                onSuccess()
            } catch (e: Exception) {
            }
        }
    }

    fun updateBook(id: Int, book: Book, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateBook(id, book)
                fetchBooks()
                onSuccess()
            } catch (e: Exception) {
            }
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteBook(id)
                fetchBooks()
            } catch (e: Exception) {
            }
        }
    }
}
