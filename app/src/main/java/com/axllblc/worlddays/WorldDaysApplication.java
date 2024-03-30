package com.axllblc.worlddays;

import android.app.Application;

import com.axllblc.worlddays.container.Container;
import com.axllblc.worlddays.container.ContainerImpl;

public class WorldDaysApplication extends Application {
    public static String USER_AGENT = "WorldDaysApplication/1.0";

    /**
     * Container for dependency injection.
     */
    private Container container;

    @Override
    public void onCreate() {
        super.onCreate();
        container = new ContainerImpl();
    }

    public Container getContainer() {
        return container;
    }
}
