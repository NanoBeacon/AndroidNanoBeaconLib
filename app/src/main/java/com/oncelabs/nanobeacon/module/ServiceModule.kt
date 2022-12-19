package com.oncelabs.nanobeacon.module

import androidx.compose.material.ExperimentalMaterialApi
import com.oncelabs.nanobeacon.manager.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@ExperimentalMaterialApi
@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideBeaconManager(impl: BeaconManagerImpl): BeaconManager
    @Binds
    abstract fun provideFilePickerManager(impl : FilePickerManagerImpl) : FilePickerManager
    @Binds
    abstract fun provideConfigDataManager(impl : ConfigDataManagerImpl) : ConfigDataManager
}