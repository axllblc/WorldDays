package com.axllblc.worlddays.data;


import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;

/**
 * Represents an event which occurs every year.
 */
@AllArgsConstructor
@Getter
@With
public class Event {
    // 👇 Basic information about the event
    /**
     * Wikidata Q-ID, such as "Q5305947".
     */
    private final @NonNull String id;
    /**
     * Event title, such as "International Day of Happiness".
     */
    private final @NonNull String title;
    /**
     * Month, between 1 and 12 (included).
     */
    private final int month;
    /**
     * Day of month, between 1 and 31.
     */
    private final int dayOfMonth;
    // 👇 Details about the event
    /**
     * URL of the Wikipedia article related to this event.
     */
    private final String wikipediaURL;
    /**
     * Introduction of the Wikipedia article related to this event.
     */
    private final String wikipediaIntro;
    /**
     * Date of creation of this event.
     */
    private final String inception;
    /**
     * Organizer of this event.
     */
    private final String organizer;
    /**
     * Founder of this event.
     */
    private final String founder;
    /**
     * URL of the official website for this event.
     */
    private final String officialWebsite;

    private final boolean isDetailed;

    public Event(@NonNull String id, @NonNull String title, int month, int dayOfMonth) {
        this.id = id;
        this.title = title;
        this.month = month;
        this.dayOfMonth = dayOfMonth;

        this.wikipediaURL = null;
        this.wikipediaIntro = null;
        this.inception = null;
        this.organizer = null;
        this.founder = null;
        this.officialWebsite = null;
        this.isDetailed = false;
    }

    public Event(@NonNull String id,
                 @NonNull String title,
                 int month,
                 int dayOfMonth,
                 String wikipediaURL,
                 String wikipediaIntro,
                 String inception,
                 String organizer,
                 String founder,
                 String officialWebsite
    ) {
        this.id = id;
        this.title = title;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.wikipediaURL = wikipediaURL;
        this.wikipediaIntro = wikipediaIntro;
        this.inception = inception;
        this.organizer = organizer;
        this.founder = founder;
        this.officialWebsite = officialWebsite;
        this.isDetailed = true;
    }

    /**
     * Returns a {@link Calendar} object containing the date of the last occurrence of this event,
     * based on current time.
     * @return Date of the last occurrence of this event
     */
    public Calendar getLastOccurrence() {
        return getOccurrence(false);
    }

    /**
     * Returns a {@link Calendar} object containing the date of the next occurrence of this event,
     * based on current time.
     * @return Date of the next occurrence of this event
     */
    public Calendar getNextOccurrence() {
        return getOccurrence(true);
    }

    /**
     * Returns a {@link Calendar} object containing the date of the occurrence of this event for
     * given year.
     * @return Date of the occurrence of this event for given year
     */
    public Calendar getOccurrenceForYear(int year) {
        return new Calendar.Builder()
                .set(Calendar.YEAR, year)
                .set(Calendar.MONTH, month)
                .set(Calendar.DAY_OF_MONTH, dayOfMonth)
                .build();
    }

    /**
     * Returns a {@link Calendar} object containing the date of the next occurrence of this event,
     * if {@code next} is true, the last occurrence otherwise.
     * @return Date of the last/next occurrence of this event
     */
    private Calendar getOccurrence(boolean next) {
        Calendar now = Calendar.getInstance();
        int currentDayOfMonth = now.get(Calendar.DAY_OF_MONTH);
        int currentMonth = now.get(Calendar.MONTH) + 1;
        int year = now.get(Calendar.YEAR);

        if (month < currentMonth || (month == currentMonth && dayOfMonth < currentDayOfMonth))
            // The occurrence for this year is in the past
            // Last occurrence → this year
            // Next occurrence → next year
            year = next ? year + 1 : year;
        else
            // The occurrence for this year is in the future
            // Last occurrence → last year
            // Next occurrence → this year
            year = next ? year - 1 : year;

        return getOccurrenceForYear(year);
    }
}
