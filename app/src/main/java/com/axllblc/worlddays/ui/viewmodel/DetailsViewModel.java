package com.axllblc.worlddays.ui.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.data.Result;
import com.axllblc.worlddays.data.repository.EventRepository;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetailsViewModel extends ViewModel {
    private final EventRepository eventRepository;

    private final MutableLiveData<DetailsUiState> uiState;


    @Inject
    public DetailsViewModel(
            EventRepository eventRepository
    ) {
        this.eventRepository = eventRepository;
        this.uiState = new MutableLiveData<>(new DetailsUiState());
    }

    public LiveData<DetailsUiState> getUiState() {
        return uiState;
    }

    public void setEventId(String eventId) {
        if (uiState.getValue().getEvent() == null
                || !uiState.getValue().getEvent().getId().equals(eventId)) {
            fetchEvent(eventId, false);
        }
    }

    private void fetchEvent(String eventId, boolean refresh) {
        ExecutorService executor = Executors.newSingleThreadExecutor();  // New thread
        Handler handler = new Handler(Looper.getMainLooper());           // Main thread

        // Callback, to be executed in the main thread
        Consumer<Result<Optional<Event>>> callback = result -> {
            if (result.hasValue() && result.get().isPresent()) {
                uiState.setValue(
                        uiState.getValue().withEvent(result.get().get())
                );
            }
            if (!result.isSuccess()) {
                uiState.setValue(
                        uiState.getValue().withException(result.getException())
                );
            }
        };

        // Fetch events in the new thread
        executor.execute(() -> {
            Result<Optional<Event>> result;
            try {
                result = eventRepository.getEvent(eventId, true, refresh);
            } catch (Exception e) {
                result = Result.error(e);
            }
            Result<Optional<Event>> finalResult = result;
            handler.post(() -> callback.accept(finalResult));
        });
    }

    public void clearException() {
        uiState.setValue(uiState.getValue().withException(null));
    }
}
