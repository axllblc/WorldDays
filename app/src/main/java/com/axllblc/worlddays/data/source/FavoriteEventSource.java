package com.axllblc.worlddays.data.source;

import com.axllblc.worlddays.data.Event;

import java.util.List;

public interface FavoriteEventSource {
    List<Event> getFavorites();
    boolean isFavorite(String eventId);
    void star(String eventId);
    void unstar(String eventId);
    void unstarAll();
}
