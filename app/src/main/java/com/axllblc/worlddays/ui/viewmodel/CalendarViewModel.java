package com.axllblc.worlddays.ui.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.data.Result;
import com.axllblc.worlddays.data.repository.EventRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CalendarViewModel extends ViewModel {
    private final EventRepository eventRepository;

    @Inject
    public CalendarViewModel(
            EventRepository eventRepository
    ) {
        super();
        this.eventRepository = eventRepository;
    }

    private final MutableLiveData<CalendarUiState> uiState =
            new MutableLiveData<>(new CalendarUiState(
                    LocalDate.now(), Collections.emptyList()
            ));

    public LiveData<CalendarUiState> getUiState() {
        return uiState;
    }

    public void setDate(LocalDate date) {
        uiState.setValue(
                uiState.getValue().withDate(date)
        );
    }

    public void fetchEventsForMonth(Month month) {
        ExecutorService executor = Executors.newSingleThreadExecutor();  // New thread
        Handler handler = new Handler(Looper.getMainLooper());           // Main thread

        // Callback, to be executed in the main thread
        Consumer<Result<List<Event>>> callback = result -> {
            uiState.setValue(
                    uiState.getValue().withEvents(result.orElse(Collections.emptyList()))
            );
            if (!result.isSuccess()) {
                uiState.setValue(
                        uiState.getValue().withException(result.getException())
                );
            }
        };

        // Fetch events in the new thread
        executor.execute(() -> {
            Result<List<Event>> result;
            try {
                result = eventRepository.getEventsByMonth(month.getValue(), true);
            } catch (Exception e) {
                result = Result.error(e);
            }
            Result<List<Event>> finalResult = result;
            handler.post(() -> callback.accept(finalResult));
        });
    }

    public void clearException() {
        uiState.setValue(uiState.getValue().withException(null));
    }

}
