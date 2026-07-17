package com.example.apirest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.apirest.data.model.Book
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun BookFormScreenPreview() {
    BookFormScreen(
        onSave = {},
        onNavigateBack = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreen(
    book: Book? = null,
    onSave: (Book) -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf(book?.title ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var genre by remember { mutableStateOf(book?.genre ?: "") }
    var year by remember { mutableStateOf(book?.year?.toString() ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (book == null) "Nuevo Libro" else "Editar Libro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it; titleError = it.isBlank() },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError,
                supportingText = { if (titleError) Text("El título es obligatorio") }
            )
            TextField(
                value = author,
                onValueChange = { author = it; authorError = it.isBlank() },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth(),
                isError = authorError,
                supportingText = { if (authorError) Text("El autor es obligatorio") }
            )
            TextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Género") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = year,
                onValueChange = { year = it; yearError = it.toIntOrNull() == null },
                label = { Text("Año") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = yearError,
                supportingText = { if (yearError) Text("El año debe ser un número") }
            )

            Button(
                onClick = {
                    val yearInt = year.toIntOrNull()
                    if (title.isNotBlank() && author.isNotBlank() && yearInt != null) {
                        onSave(Book(id = book?.id, title = title, author = author, genre = genre, year = yearInt))
                    } else {
                        titleError = title.isBlank()
                        authorError = author.isBlank()
                        yearError = yearInt == null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
