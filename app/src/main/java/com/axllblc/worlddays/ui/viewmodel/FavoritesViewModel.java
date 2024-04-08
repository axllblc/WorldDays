package com.axllblc.worlddays.ui.viewmodel;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.data.Result;
import com.axllblc.worlddays.data.repository.EventRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoritesViewModel extends ViewModel {
    private final EventRepository eventRepository;

    private final MutableLiveData<FavoritesUiState> uiState;

    @Inject
    public FavoritesViewModel(
            EventRepository eventRepository
    ) {
        super();
        this.eventRepository = eventRepository;

        this.uiState = new MutableLiveData<>(new FavoritesUiState());
    }

    public LiveData<FavoritesUiState> getUiState() {
        return uiState;
    }

    public void fetch() {
        ExecutorService executor = Executors.newSingleThreadExecutor();  // New thread
        Handler handler = new Handler(Looper.getMainLooper());           // Main thread

        // Callback, to be executed in the main thread
        Consumer<Result<List<Event>>> callback = result -> {
            if (result.hasValue()) {
                uiState.setValue(
                        uiState.getValue().withEvents(result.get()).withException(null)
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
            Result<List<Event>> result;
            try {
                result = Result.success(eventRepository.getFavorites());
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
