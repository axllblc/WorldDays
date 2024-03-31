package com.axllblc.worlddays.data.source;

import com.axllblc.worlddays.data.Event;

import java.util.List;

public interface WritableEventSource {
    void insert(Event event);
    void insertAll(List<Event> events);
    void update(Event event);
    void delete(Event event);
    void delete(String eventId);
    void deleteAll();
}
