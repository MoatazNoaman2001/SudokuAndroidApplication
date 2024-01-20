package com.example.simplesudokuapp.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object ProductionDispatcherProvider : DispatcherProvider  {
    override fun provideUIContext(): CoroutineContext {
        return Dispatchers.Main
    }

    override fun provideIoContext(): CoroutineContext {
        return Dispatchers.IO
    }
}