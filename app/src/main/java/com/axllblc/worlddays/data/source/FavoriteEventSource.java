package com.axllblc.worlddays.data.source;

import com.axllblc.worlddays.data.Event;

import java.util.List;

public interface FavoriteEventSource {
    List<Event> getFavorites();
    boolean isFavorite(Event event);
    void star(Event event);
    void unstar(Event event);
    void unstarAll();
}
