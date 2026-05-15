package com.example.karunada_kala.di

import com.example.karunada_kala.data.repository.FirestoreDataRepositoryImpl
import com.example.karunada_kala.data.repository.FirebaseAuthRepositoryImpl
import com.example.karunada_kala.domain.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataRepository(): DataRepository {
        return FirestoreDataRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): com.example.karunada_kala.domain.repository.AuthRepository {
        return FirebaseAuthRepositoryImpl()
    }
}
