package com.axllblc.worlddays.ui.viewmodel;

import com.axllblc.worlddays.data.Event;

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
public class DetailsUiState {
    private Event event = null;
    private Throwable exception = null;
    private Boolean isFavorite = null;
}
