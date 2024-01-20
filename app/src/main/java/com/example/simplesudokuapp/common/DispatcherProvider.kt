package com.example.simplesudokuapp.common

import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {
    fun provideUIContext() : CoroutineContext
    fun provideIoContext() : CoroutineContext
}