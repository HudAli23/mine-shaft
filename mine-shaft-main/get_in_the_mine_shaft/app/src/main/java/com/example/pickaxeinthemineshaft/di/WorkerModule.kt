package com.example.pickaxeinthemineshaft.di

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.pickaxeinthemineshaft.workers.ReminderWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    @Provides
    @Singleton
    fun provideWorkerFactory(
        workerFactories: Map<String, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
    ): WorkerFactory {
        return DelegatingWorkerFactory(workerFactories)
    }

    @Provides
    @IntoMap
    @StringKey("ReminderWorker")
    fun provideReminderWorkerFactory(
        factory: ReminderWorker.Factory
    ): ChildWorkerFactory = factory
}

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): androidx.work.ListenableWorker
}

class DelegatingWorkerFactory @Inject constructor(
    private val workerFactories: Map<String, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): androidx.work.ListenableWorker? {
        val factoryProvider = workerFactories[workerClassName.substringAfterLast('.')]
            ?: return null
        return factoryProvider.get().create(appContext, workerParameters)
    }
} 