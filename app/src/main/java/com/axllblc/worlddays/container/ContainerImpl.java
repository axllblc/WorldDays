package com.axllblc.worlddays.container;

import okhttp3.OkHttpClient;

public class ContainerImpl implements Container {
    private static OkHttpClient okHttpClient;

    @Override
    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null)
            okHttpClient = new OkHttpClient();
        return okHttpClient;
    }
}
