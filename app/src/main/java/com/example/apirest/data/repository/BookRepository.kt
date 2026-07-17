package com.example.apirest.data.repository

import com.example.apirest.data.model.Book
import com.example.apirest.data.network.BookApiService

class BookRepository(private val apiService: BookApiService) {
    suspend fun getBooks() = apiService.getBooks()
    suspend fun addBook(book: Book) = apiService.addBook(book)
    suspend fun updateBook(id: Int, book: Book) = apiService.updateBook(id, book)
    suspend fun deleteBook(id: Int) = apiService.deleteBook(id)
}
