package com.axllblc.worlddays.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CalendarViewModel extends ViewModel {
    private final MutableLiveData<LocalDate> mDate;

    @Inject
    public CalendarViewModel() {
        super();

        mDate = new MutableLiveData<>(LocalDate.now());
    }

    public LiveData<LocalDate> getDate() {
        return mDate;
    }

    public void setDate(LocalDate date) {
        mDate.setValue(date);
    }
}
