package com.example.apirest.data.network

import com.example.apirest.data.model.Book
import retrofit2.http.*

interface BookApiService {
    @GET("books")
    suspend fun getBooks(): List<Book>

    @POST("books")
    suspend fun addBook(@Body book: Book): Book

    @PUT("books/{id}")
    suspend fun updateBook(@Path("id") id: Int, @Body book: Book): Book

    @DELETE("books/{id}")
    suspend fun deleteBook(@Path("id") id: Int): Unit
}
