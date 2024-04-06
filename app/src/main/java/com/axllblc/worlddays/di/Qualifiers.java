package com.axllblc.worlddays.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

public class Qualifiers {
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface WikidataEventSource {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LocalDatabaseEventSource {}
}
