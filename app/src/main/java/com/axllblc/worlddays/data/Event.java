package com.axllblc.worlddays.data;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.With;

/**
 * Represents an event which occurs every year.
 */
@AllArgsConstructor
@Getter
@With
@ToString
@EqualsAndHashCode
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
     * Day in year for periodic occurrence.
     */
    private final @NonNull MonthDay monthDay;
    // 👇 Details about the event
    /**
     * URL of the Wikipedia article related to this event.
     * [optional]
     */
    private final String wikipediaURL;
    /**
     * Introduction of the Wikipedia article related to this event.
     * [optional]
     */
    private final String wikipediaIntro;
    /**
     * Date of creation of this event.
     * [optional]
     */
    private final LocalDate inception;
    /**
     * Founder of this event.
     * [optional]
     */
    private final String founder;

    private final boolean isDetailed;

    public Event(@NonNull String id, @NonNull String title, @NonNull MonthDay monthDay) {
        this.id = id;
        this.title = title;
        this.monthDay = monthDay;

        this.wikipediaURL = null;
        this.wikipediaIntro = null;
        this.inception = null;
        this.founder = null;
        this.isDetailed = false;
    }

    public Event(@NonNull String id,
                 @NonNull String title,
                 @NonNull MonthDay monthDay,
                 String wikipediaURL,
                 String wikipediaIntro,
                 LocalDate inception,
                 String founder
    ) {
        this.id = id;
        this.title = title;
        this.monthDay = monthDay;
        this.wikipediaURL = wikipediaURL;
        this.wikipediaIntro = wikipediaIntro;
        this.inception = inception;
        this.founder = founder;
        this.isDetailed = true;
    }

    /**
     * Returns a {@link LocalDate} object containing the date of the last occurrence of this event,
     * based on current time.
     *
     * @return Date of the last occurrence of this event
     */
    public LocalDate getLastOccurrence() {
        LocalDate thisYearOccurrence = monthDay.atYear(Year.now().getValue());
        if (thisYearOccurrence.isBefore(LocalDate.now()))
            // The occurrence for this year is in the past
            // Last occurrence → this year
            return thisYearOccurrence;
        else
            // The occurrence for this year is in the future
            // Last occurrence → last year
            return thisYearOccurrence.minusYears(1);
    }

    /**
     * Returns a {@link LocalDate} object containing the date of the next occurrence of this event,
     * based on current time.
     *
     * @return Date of the next occurrence of this event
     */
    public LocalDate getNextOccurrence() {
        LocalDate thisYearOccurrence = monthDay.atYear(Year.now().getValue());
        if (thisYearOccurrence.isBefore(LocalDate.now()))
            // The occurrence for this year is in the past
            // Next occurrence → next year
            return thisYearOccurrence.plusYears(1);
        else
            // The occurrence for this year is in the future
            // Next occurrence → this year
            return thisYearOccurrence;
    }

    /**
     * Returns a {@link LocalDate} object containing the date of the occurrence of this event for
     * given year.
     *
     * @return Date of the occurrence of this event for given year
     */
    public LocalDate getOccurrenceForYear(int year) {
        return monthDay.atYear(year);
    }

    public boolean hasSameId(Event other) {
        return id.equals(other.id);
    }
}
