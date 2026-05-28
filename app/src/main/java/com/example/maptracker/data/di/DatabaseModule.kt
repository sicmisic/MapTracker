package com.example.maptracker.data.di

import android.content.Context
import androidx.room.Room
import com.example.maptracker.data.local.AppDatabase
import com.example.maptracker.data.local.LocationDao
import com.example.maptracker.data.repository.LocationRepositoryImpl
import com.example.maptracker.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDao = database.locationDao()

    @Provides
    @Singleton
    fun provideLocationRepository(dao: LocationDao): LocationRepository =
        LocationRepositoryImpl(dao)
}
