package com.axllblc.worlddays.data.repository;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.data.Result;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Result<Optional<Event>> getEvent(String id, boolean withDetails, boolean refresh);
    Result<List<Event>> getAll(boolean refresh);
    Result<List<Event>> getEventsByName(String str, boolean refresh) throws Exception;
    Result<List<Event>> getEventsByMonth(int month, boolean refresh) throws Exception;

    List<Event> getFavorites();
    boolean isFavorite(String eventId);
    void star(String eventId);
    void unstar(String eventId);
    void unstarAll();
}
