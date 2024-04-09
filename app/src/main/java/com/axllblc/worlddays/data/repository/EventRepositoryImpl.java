package com.axllblc.worlddays.data.repository;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.data.Result;
import com.axllblc.worlddays.data.source.ReadWriteEventSource;
import com.axllblc.worlddays.data.source.ReadableEventSource;
import com.axllblc.worlddays.data.source.WikipediaIntroSource;
import com.axllblc.worlddays.di.Qualifiers;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class EventRepositoryImpl implements EventRepository {
    ReadableEventSource remoteEventSource;
    ReadWriteEventSource localEventSource;
    WikipediaIntroSource wikipediaIntroSource;

    @Inject
    public EventRepositoryImpl(
            @Qualifiers.WikidataEventSource ReadableEventSource remoteEventSource,
            @Qualifiers.LocalDatabaseEventSource ReadWriteEventSource localEventSource,
            WikipediaIntroSource wikipediaIntroSource
    ) {
        this.remoteEventSource = remoteEventSource;
        this.localEventSource = localEventSource;
        this.wikipediaIntroSource = wikipediaIntroSource;
    }

    @Override
    public Result<Optional<Event>> getEvent(String id, boolean withDetails, boolean refresh) {
        try {
            // Fetch from local database
            Optional<Event> eventFromLocalSource = localEventSource.getEvent(id, withDetails);

            if (refresh || !eventFromLocalSource.isPresent()) {
                // Fetch from remote source
                Optional<Event> eventFromRemoteSource;
                try {
                    eventFromRemoteSource = remoteEventSource.getEvent(id, withDetails);

                    if (withDetails
                            && eventFromRemoteSource.isPresent()
                            && eventFromRemoteSource.get().getWikipediaURL() != null
                    ) {
                        // Fetch intro
                        String intro = wikipediaIntroSource.getArticleIntro(
                                eventFromRemoteSource.get().getWikipediaURL()
                        ).orNull();

                        if (intro != null) {
                            eventFromRemoteSource = Optional.of(
                                    eventFromRemoteSource.get().withWikipediaIntro(intro)
                            );
                        }
                    }
                } catch (Exception e) {
                    return Result.error(e, eventFromLocalSource);
                }

                merge(
                        eventFromRemoteSource.orElse(null),
                        eventFromLocalSource.orElse(null)
                );

                return Result.success(eventFromRemoteSource);
            } else {
                return Result.success(eventFromLocalSource);
            }
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<List<Event>> getAll(boolean refresh) {
        try {
            List<Event> eventsFromLocalSource = localEventSource.getAll();

            if (refresh || eventsFromLocalSource.isEmpty()) {
                // Fetch from remote source
                List<Event> eventsFromRemoteSource;
                try {
                    eventsFromRemoteSource = remoteEventSource.getAll();
                } catch (Exception e) {
                    return Result.error(e, eventsFromLocalSource);
                }

                merge(eventsFromRemoteSource, eventsFromLocalSource);

                return Result.success(eventsFromRemoteSource);
            } else {
                return Result.success(eventsFromLocalSource);
            }
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<List<Event>> getEventsByName(String str, boolean refresh) {
        try {
            List<Event> eventsFromLocalSource = localEventSource.getEventsByName(str);

            if (refresh || eventsFromLocalSource.isEmpty()) {
                // Fetch from remote source
                List<Event> eventsFromRemoteSource;
                try {
                    eventsFromRemoteSource = remoteEventSource.getEventsByName(str);
                } catch (Exception e) {
                    return Result.error(e, eventsFromLocalSource);
                }

                merge(eventsFromRemoteSource, eventsFromLocalSource);

                return Result.success(eventsFromRemoteSource);
            } else {
                return Result.success(eventsFromLocalSource);
            }
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public Result<List<Event>> getEventsByMonth(int month, boolean refresh) {
        try {
            List<Event> eventsFromLocalSource = localEventSource.getEventsByMonth(month);

            if (refresh || eventsFromLocalSource.isEmpty()) {
                // Fetch from remote source
                List<Event> eventsFromRemoteSource;
                try {
                    eventsFromRemoteSource = remoteEventSource.getEventsByMonth(month);
                } catch (Exception e) {
                    return Result.error(e, eventsFromLocalSource);
                }

                merge(eventsFromRemoteSource, eventsFromLocalSource);

                return Result.success(eventsFromRemoteSource);
            } else {
                return Result.success(eventsFromLocalSource);
            }
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    @Override
    public List<Event> getFavorites() {
        return localEventSource.getFavorites();
    }

    @Override
    public boolean isFavorite(String eventId) {
        return localEventSource.isFavorite(eventId);
    }

    @Override
    public void star(String eventId) {
        localEventSource.star(eventId);
    }

    @Override
    public void unstar(String eventId) {
        localEventSource.unstar(eventId);
    }

    @Override
    public void unstarAll() {
        localEventSource.unstarAll();
    }

    private void merge(Event eventFromRemoteSource, Event eventFromLocalSource) {
        if (eventFromRemoteSource != null) {
            if (eventFromLocalSource == null) {
                localEventSource.insert(eventFromRemoteSource);
            } else if (!eventFromRemoteSource.equals(eventFromLocalSource)) {
                localEventSource.update(eventFromRemoteSource);
            }
        }
    }

    private void merge(List<Event> eventsFromRemoteSource, List<Event> eventsFromLocalSource) {
        // Remove events which are not in eventsFromRemoteSource
        eventsFromLocalSource.stream()
                .filter(event -> eventsFromRemoteSource.stream().noneMatch(e -> e.hasSameId(event)))
                .forEach(event -> localEventSource.delete(event));

        // Update events
        eventsFromRemoteSource.stream()
                .filter(event -> eventsFromLocalSource.stream()
                        .anyMatch(e -> e.hasSameId(event) && !e.equals(event))
                )   /* ⇒ Events to update */
                .forEach(event -> localEventSource.update(event));

        // Insert events which are not in eventsFromLocalSource
        eventsFromRemoteSource.stream()
                .filter(event -> eventsFromLocalSource.stream().noneMatch(e -> e.hasSameId(event)))
                .forEach(event -> localEventSource.insert(event));
    }
}
