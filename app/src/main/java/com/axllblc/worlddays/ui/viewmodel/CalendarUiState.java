package com.axllblc.worlddays.ui.viewmodel;

import com.axllblc.worlddays.data.Event;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@With
@ToString
@EqualsAndHashCode
public class CalendarUiState {
    private final LocalDate date;
    private final List<Event> events;
    private Throwable exception = null;
}
