package com.axllblc.worlddays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.data.source.LocalDatabaseEventSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(AndroidJUnit4.class)
public class LocalDatabaseEventSourceTest {
    LocalDatabaseEventSource source;

    static Event event1 = new Event(
            "Q5305947",
            "International Day of Happiness",
            MonthDay.of(3, 20)
    );

    static Event detailedEvent1 = new Event(
            "Q5305947",
            "International Day of Happiness",
            MonthDay.of(3, 20),
            "https://en.wikipedia.org/wiki/International_Day_of_Happiness",
            "The International Day of Happiness is celebrated throughout the world on 20 March. It was established by the United Nations General Assembly on 28 June 2012.",
            LocalDate.of(2013, 1, 1),
            null
    );

    static Event event2 = new Event(
            "Q2603487",
            "World Day of Peace",
            MonthDay.of(1, 1)
    );

    static Event event3 = new Event(
            "Q104850441",
            "World Logic Day",
            MonthDay.of(1, 14)
    );

    @Before
    public void setUp() {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        source = new LocalDatabaseEventSource(context);
        source.deleteAll();
    }

    @Test
    public void emptyDatabase_getAll_returnsEmptyList() {
        assertTrue(source.getAll().isEmpty());
    }

    @Test
    public void emptyDatabase_getEvent_returnsEmptyOptional() {
        assertFalse(source.getEvent("Q5305947", false).isPresent());
        assertFalse(source.getEvent("Q5305947", true).isPresent());
    }

    @Test
    public void insertEvent_getEvent_returnsEvent() {
        source.insert(event1);

        Optional<Event> actualEvent = source.getEvent(event1.getId(), false);

        assertTrue(actualEvent.isPresent());
        assertEquals(event1, actualEvent.get());
    }

    @Test
    public void insertDetailedEvent_getDetailedEvent_returnsDetailedEvent() {
        source.insert(detailedEvent1);

        Optional<Event> actualEvent = source.getEvent(detailedEvent1.getId(), true);

        assertTrue(actualEvent.isPresent());
        assertEquals(detailedEvent1, actualEvent.get());
    }

    @Test
    public void getAllEvents_returnsAllEvents() {
        List<Event> events = new ArrayList<>(3);
        events.add(event1);
        events.add(event2);
        events.add(event3);

        source.insertAll(events);

        List<Event> actualEvents = source.getAll();

        assertEquals(events.size(), actualEvents.size());
        assertTrue(actualEvents.containsAll(events));
    }

    @Test
    public void getEventsByName_returnsExpectedEvents() {
        List<Event> events = new ArrayList<>(3);
        events.add(event1);
        events.add(event2);
        events.add(event3);

        source.insertAll(events);

        List<Event> actualEvents = source.getEventsByName("HAPPINESS");

        assertEquals(1, actualEvents.size());
        assertTrue(actualEvents.contains(event1));
    }

    @Test
    public void getEventsByMonth_returnsExpectedEvents() {
        List<Event> events = new ArrayList<>(3);
        events.add(event1);
        events.add(event2);
        events.add(event3);

        source.insertAll(events);

        List<Event> actualEvents = source.getEventsByMonth(1);

        assertEquals(2, actualEvents.size());
        assertTrue(actualEvents.contains(event2));
        assertTrue(actualEvents.contains(event3));
    }

    @Test
    public void delete() {
        List<Event> events = new ArrayList<>(3);
        events.add(event1);
        events.add(event2);
        events.add(event3);

        source.insertAll(events);

        source.delete(event3);

        List<Event> actualEvents = source.getAll();

        assertEquals(2, actualEvents.size());
        assertTrue(actualEvents.contains(event1));
        assertTrue(actualEvents.contains(event2));
    }

    @Test
    public void deleteAll() {
        List<Event> events = new ArrayList<>(3);
        events.add(event1);
        events.add(event2);
        events.add(event3);

        source.insertAll(events);

        source.deleteAll();

        assertTrue(source.getAll().isEmpty());
    }



//    getEvent(String, boolean)
//    getAll()
//    getEventsByName(String)
//    getEventsByMonth(int)
//    insert(Event)
//    update(Event)
//    delete(Event)
//    delete(String)
//    deleteAll()
}
