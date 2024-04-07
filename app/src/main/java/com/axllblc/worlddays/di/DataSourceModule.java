package com.axllblc.worlddays.di;

import com.axllblc.worlddays.data.source.LocalDatabaseEventSource;
import com.axllblc.worlddays.data.source.ReadWriteEventSource;
import com.axllblc.worlddays.data.source.ReadableEventSource;
import com.axllblc.worlddays.data.source.WikidataEventSource;
import com.axllblc.worlddays.data.source.WikipediaIntroSource;
import com.axllblc.worlddays.data.source.WikipediaIntroSourceImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DataSourceModule {
    @Singleton
    @Binds
    public abstract WikipediaIntroSource bindWikipediaIntroSource(WikipediaIntroSourceImpl impl);

    @Singleton
    @Binds
    @Qualifiers.WikidataEventSource
    public abstract ReadableEventSource bindWikidataEventSource(WikidataEventSource impl);

    @Singleton
    @Binds
    @Qualifiers.LocalDatabaseEventSource
    public abstract ReadWriteEventSource bindLocalDatabaseEventSource(LocalDatabaseEventSource impl);
}
