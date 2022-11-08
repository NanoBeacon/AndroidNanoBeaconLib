package com.oncelabs.nanobeacon.module

import com.oncelabs.nanobeacon.manager.BeaconManager
import com.oncelabs.nanobeacon.manager.BeaconManagerImpl
import com.oncelabs.nanobeacon.manager.FilePickerManager
import com.oncelabs.nanobeacon.manager.FilePickerManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideBeaconManager(impl: BeaconManagerImpl): BeaconManager
    @Binds
    abstract fun provideFilePickerManager(impl : FilePickerManagerImpl) : FilePickerManager

}