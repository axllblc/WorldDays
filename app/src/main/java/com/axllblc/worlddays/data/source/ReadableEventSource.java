package com.axllblc.worlddays.data.source;

import com.axllblc.worlddays.data.Event;

import java.util.List;
import java.util.Optional;

public interface ReadableEventSource {
    /**
     * Returns the {@link Event} with given {@code id}, if it exists.
     *
     * @param id Wikidata Q-ID, such as "Q5305947".
     * @param withDetails {@code true} to get details (wikipediaURL, wikipediaIntro, inception, founder)
     * @return An {@link Optional} instance containing the {@link Event} with given {@code id}, if it exists.
     */
    Optional<Event> getEvent(String id, boolean withDetails) throws Exception;
    List<Event> getAll() throws Exception;
    List<Event> getEventsByName(String str) throws Exception;
    List<Event> getEventsByMonth(int month) throws Exception;
}
