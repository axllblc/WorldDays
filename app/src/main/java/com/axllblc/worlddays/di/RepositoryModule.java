package com.axllblc.worlddays.di;

import com.axllblc.worlddays.data.repository.EventRepository;
import com.axllblc.worlddays.data.repository.EventRepositoryImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {
    @Singleton
    @Binds
    public abstract EventRepository bindEventRepository(EventRepositoryImpl impl);
}
