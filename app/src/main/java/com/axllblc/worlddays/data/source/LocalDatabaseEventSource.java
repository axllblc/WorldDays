package com.axllblc.worlddays.data.source;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.axllblc.worlddays.data.Event;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class LocalDatabaseEventSource extends SQLiteOpenHelper implements ReadWriteEventSource {
    private static final String DB_NAME = "world_days.db";
    private static final int VERSION = 1;

    public @Inject LocalDatabaseEventSource(@ApplicationContext Context context) {
        super(context, DB_NAME, null, VERSION);
    }


    // ReadableEventSource implementation

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DBContract.Events.TABLE_NAME + " (" +
                // List of attributes
                Arrays.stream(DBContract.Events.values())
                        .map(attribute -> attribute + " " + attribute.sqlType)
                        .collect(Collectors.joining(", ")) +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + DBContract.Events.TABLE_NAME;
        db.execSQL(query);
        onCreate(db);
    }

    @Override
    public Optional<Event> getEvent(String id, boolean withDetails) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String[] projection = {"*"};
            String selection = DBContract.Events.ID + " = ?";
            String[] selectionArgs = {id};

            try (Cursor cursor = db.query(
                    DBContract.Events.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null)
            ) {
                if (cursor.moveToNext()) {
                    Event event = cursorToEvent(cursor, withDetails);
                    if (withDetails && !event.isDetailed()) return Optional.empty();
                    return Optional.of(event);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public List<Event> getAll() {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String[] projection = {"*"};

            try (Cursor cursor = db.query(
                    DBContract.Events.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null)
            ) {
                List<Event> events = new ArrayList<>();

                while (cursor.moveToNext()) {
                    events.add(cursorToEvent(cursor, false));
                }

                return events;
            }
        }
    }

    @Override
    public List<Event> getEventsByName(String str) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String[] projection = {"*"};
            String selection = DBContract.Events.TITLE + " LIKE '%' || ? || '%'";
            String[] selectionArgs = {str};

            try (Cursor cursor = db.query(
                    DBContract.Events.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null)
            ) {
                List<Event> events = new ArrayList<>();

                while (cursor.moveToNext()) {
                    events.add(cursorToEvent(cursor, false));
                }

                return events;
            }
        }
    }

    @Override
    public List<Event> getEventsByMonth(int month) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String[] projection = {"*"};
            String selection = DBContract.Events.MONTH + " = " + month;

            try (Cursor cursor = db.query(
                    DBContract.Events.TABLE_NAME,
                    projection,
                    selection,
                    null,
                    null,
                    null,
                    null)
            ) {
                List<Event> events = new ArrayList<>();

                while (cursor.moveToNext()) {
                    events.add(cursorToEvent(cursor, false));
                }

                return events;
            }
        }
    }

    private static Event cursorToEvent(Cursor cursor, boolean withDetails) {
        String id = cursor
                .getString(cursor.getColumnIndexOrThrow(DBContract.Events.ID.toString()));
        String title = cursor
                .getString(cursor.getColumnIndexOrThrow(DBContract.Events.TITLE.toString()));
        int month = cursor
                .getInt(cursor.getColumnIndexOrThrow(DBContract.Events.MONTH.toString()));
        int dayOfMonth = cursor
                .getInt(cursor.getColumnIndexOrThrow(DBContract.Events.DAY_OF_MONTH.toString()));
        MonthDay monthDay = MonthDay.of(month, dayOfMonth);
        boolean detailsFetched = 1 == cursor
                .getInt(cursor.getColumnIndexOrThrow(DBContract.Events.DETAILS_FETCHED.toString()));

        if (withDetails && detailsFetched) {
            String wikipediaUrl = cursor
                    .getString(cursor.getColumnIndexOrThrow(DBContract.Events.WIKIPEDIA_URL.toString()));
            String wikipediaIntro = cursor
                    .getString(cursor.getColumnIndexOrThrow(DBContract.Events.WIKIPEDIA_INTRO.toString()));
            String inceptionString = cursor
                    .getString(cursor.getColumnIndexOrThrow(DBContract.Events.INCEPTION.toString()));
            LocalDate inception = inceptionString != null ? LocalDate.parse(inceptionString) : null;
            String founder = cursor
                    .getString(cursor.getColumnIndexOrThrow(DBContract.Events.FOUNDER.toString()));

            return new Event(id, title, monthDay, wikipediaUrl, wikipediaIntro, inception, founder);
        } else {
            return new Event(id, title, monthDay);
        }
    }


    // WritableEventSource implementation

    @Override
    public void insert(Event event) {
        List<Event> list = new ArrayList<>(1);
        list.add(event);
        insertAll(list);
    }

    @Override
    public void insertAll(List<Event> events) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            for (Event event: events) {
                ContentValues row = new ContentValues();
                row.put(DBContract.Events.ID.toString(), event.getId());
                row.put(DBContract.Events.TITLE.toString(), event.getTitle());
                row.put(DBContract.Events.MONTH.toString(), event.getMonthDay().getMonthValue());
                row.put(DBContract.Events.DAY_OF_MONTH.toString(), event.getMonthDay().getDayOfMonth());

                row.put(DBContract.Events.DETAILS_FETCHED.toString(), event.isDetailed());
                if (event.isDetailed()) {
                    row.put(DBContract.Events.WIKIPEDIA_URL.toString(), event.getWikipediaURL());
                    row.put(DBContract.Events.WIKIPEDIA_INTRO.toString(), event.getWikipediaIntro());
                    row.put(DBContract.Events.INCEPTION.toString(),
                            event.getInception() != null ? event.getInception().toString() : null
                    );
                    row.put(DBContract.Events.FOUNDER.toString(), event.getFounder());
                }

                long result = db.insert(DBContract.Events.TABLE_NAME, null, row);
                if (result == -1) throw new RuntimeException();
            }
        }
    }

    @Override
    public void update(Event event) {
        delete(event.getId());
        insert(event);
    }

    @Override
    public void delete(Event event) {
        delete(event.getId());
    }

    @Override
    public void delete(String eventId) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.delete(DBContract.Events.TABLE_NAME, "ID = ?", new String[]{eventId});
        }
    }

    @Override
    public void deleteAll() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            db.delete(DBContract.Events.TABLE_NAME, null, null);
        }
    }


    // FavoriteEventSource implementation

    @Override
    public List<Event> getFavorites() {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String[] projection = {"*"};

            try (Cursor cursor = db.query(
                    DBContract.Events.TABLE_NAME,
                    projection,
                    DBContract.Events.USER_FAVORITE + " = 1",
                    null,
                    null,
                    null,
                    null)
            ) {
                List<Event> events = new ArrayList<>();

                while (cursor.moveToNext()) {
                    events.add(cursorToEvent(cursor, false));
                }

                return events;
            }
        }
    }

    @Override
    public boolean isFavorite(String eventId) {
        try (SQLiteDatabase db = getReadableDatabase()) {
            String[] projection = {"*"};

            String selection = DBContract.Events.ID + " = ? AND "
                    + DBContract.Events.USER_FAVORITE + " = 1";
            String[] selectionArgs = {eventId};

            try (Cursor cursor = db.query(
                    DBContract.Events.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null)
            ) {
                return cursor.moveToNext();
                // Returns true if there is an event
            }
        }
    }

    @Override
    public void star(String eventId) {
        setFavorite(eventId, true);
    }

    @Override
    public void unstar(String eventId) {
        setFavorite(eventId, false);
    }

    @Override
    public void unstarAll() {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.Events.USER_FAVORITE.toString(), false);

            db.update(DBContract.Events.TABLE_NAME, contentValues, null, null);
        }
    }

    private void setFavorite(String eventId, boolean star) {
        try (SQLiteDatabase db = getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.Events.USER_FAVORITE.toString(), star);

            String whereClause = DBContract.Events.ID + " = ?";
            String[] whereArgs = {eventId};

            db.update(DBContract.Events.TABLE_NAME, contentValues, whereClause, whereArgs);
        }
    }

    /**
     * Defines the tables of the database.
     */
    static class DBContract {
        enum Events {
            ID("TEXT PRIMARY KEY"),
            TITLE("TEXT"),
            MONTH("INTEGER"),
            DAY_OF_MONTH("INTEGER"),
            WIKIPEDIA_URL("TEXT DEFAULT NULL"),
            WIKIPEDIA_INTRO("TEXT DEFAULT NULL"),
            INCEPTION("TEXT DEFAULT NULL"),
            FOUNDER("TEXT DEFAULT NULL"),
            DETAILS_FETCHED("BOOLEAN DEFAULT FALSE"),
            USER_FAVORITE("BOOLEAN DEFAULT FALSE"),
            ;

            public static final String TABLE_NAME = "events";
            public final String sqlType;

            Events(String sqlType) {
                this.sqlType = sqlType;
            }
        }
    }
}
