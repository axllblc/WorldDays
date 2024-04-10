package com.axllblc.worlddays.ui.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.data.Result;
import com.axllblc.worlddays.data.repository.EventRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DetailsViewModel extends ViewModel {
    private static final String TAG = "DetailsViewModel";
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

    /**
     * Set the ID of the {@link Event} to show.
     * @param eventId ID of the {@link Event} to show
     */
    public void setEventId(String eventId) {
        Event event = Objects.requireNonNull(uiState.getValue()).getEvent();
        if (event == null || !event.getId().equals(eventId)) {
            fetchEvent(eventId, false);
        }
    }

    private void fetchEvent(String eventId, boolean refresh) {
        ExecutorService executor = Executors.newSingleThreadExecutor();  // New thread
        Handler handler = new Handler(Looper.getMainLooper());           // Main thread

        // Callback, to be executed in the main thread
        Consumer< Pair<Result<Optional<Event>>, Boolean> > callback = pair -> {
            if (pair.first.hasValue() && pair.first.get().isPresent()) {
                uiState.setValue(
                        uiState.getValue().withEvent(pair.first.get().get())
                                .withIsFavorite(pair.second)
                );
            }
            if (!pair.first.isSuccess()) {
                uiState.setValue(
                        uiState.getValue().withException(pair.first.getException())
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

            Boolean isFavorite = null;
            try {
                isFavorite = eventRepository.isFavorite(eventId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to check favorite status", e);
            }
            Boolean finalIsFavorite = isFavorite;

            handler.post(() -> callback.accept(
                    new Pair<>(finalResult, finalIsFavorite)
            ));
        });
    }

    public void clearException() {
        uiState.setValue(uiState.getValue().withException(null));
    }

    public void setFavorite(boolean favorite) {
        ExecutorService executor = Executors.newSingleThreadExecutor();  // New thread
        Handler handler = new Handler(Looper.getMainLooper());           // Main thread

        // Callback, to be executed in the main thread
        Consumer<Result<Boolean>> callback = result -> {
            if (result.isSuccess()) {
                uiState.setValue(
                        uiState.getValue().withIsFavorite(result.get())
                );
            } else {
                uiState.setValue(
                        uiState.getValue().withException(result.getException())
                );
            }
        };

        // Set favorite in the new thread
        executor.execute(() -> {
            Result<Boolean> result;
            try {
                String eventId = Objects.requireNonNull(uiState.getValue()).getEvent().getId();
                if (favorite) {
                    eventRepository.star(eventId);
                } else {
                    eventRepository.unstar(eventId);
                }
                result = Result.success(favorite);
            } catch (Exception e) {
                result = Result.error(e);
            }
            Result<Boolean> finalResult = result;

            handler.post(() -> callback.accept(finalResult));
        });
    }
}
