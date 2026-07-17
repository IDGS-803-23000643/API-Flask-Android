package com.example.apirest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.apirest.data.model.Book
import com.example.apirest.ui.screens.BookFormScreen
import com.example.apirest.ui.screens.BookListScreen
import com.example.apirest.ui.viewmodel.BookViewModel
import com.example.apirest.ui.viewmodel.UiState
import com.google.gson.Gson

@Composable
fun NavGraph(navController: NavHostController, viewModel: BookViewModel) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            BookListScreen(
                viewModel = viewModel,
                onAddBook = { navController.navigate("form") },
                onEditBook = { book ->
                    val bookJson = Gson().toJson(book)
                    navController.navigate("form?book=$bookJson")
                }
            )
        }
        composable(
            "form?book={book}",
            arguments = listOf(navArgument("book") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val bookJson = backStackEntry.arguments?.getString("book")
            val book = bookJson?.let { Gson().fromJson(it, Book::class.java) }
            BookFormScreen(
                book = book,
                onSave = { updatedBook ->
                    if (updatedBook.id == null) {
                        viewModel.addBook(updatedBook) { navController.popBackStack() }
                    } else {
                        viewModel.updateBook(updatedBook.id, updatedBook) { navController.popBackStack() }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
