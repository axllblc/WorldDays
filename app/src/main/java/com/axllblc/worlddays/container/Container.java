package com.axllblc.worlddays.container;

import okhttp3.OkHttpClient;

/**
 * Container for dependency injection.
 */
public interface Container {
    /**
     * Returns a single {@link OkHttpClient} instance.
     * @return {@link OkHttpClient} instance.
     */
    OkHttpClient getOkHttpClient();
}
