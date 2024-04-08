package com.axllblc.worlddays.ui.viewmodel;

import com.axllblc.worlddays.data.Event;

import java.util.Collections;
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
public class FavoritesUiState {
    private List<Event> events = Collections.emptyList();
    private Throwable exception = null;
}
