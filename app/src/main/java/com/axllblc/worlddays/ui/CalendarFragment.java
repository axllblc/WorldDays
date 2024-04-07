package com.axllblc.worlddays.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.axllblc.worlddays.MainActivity;
import com.axllblc.worlddays.databinding.FragmentCalendarBinding;
import com.axllblc.worlddays.ui.viewmodel.CalendarViewModel;

import java.time.LocalDate;
import java.time.ZoneId;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class CalendarFragment extends Fragment {
    private CalendarViewModel calendarViewModel;

    private FragmentCalendarBinding binding;

    // the fragment initialization parameters
    public static final String ARG_DATE = "date";

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param date Date to use in CalendarFragment
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance(@NonNull LocalDate date) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of this fragment, with default parameters.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance() {
        return newInstance(LocalDate.now());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        LocalDate dateParam;
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                dateParam = getArguments().getSerializable(ARG_DATE, LocalDate.class);
            } else {
                dateParam = (LocalDate) getArguments().getSerializable(ARG_DATE);
            }
            if (dateParam != null) {
                calendarViewModel.setDate(dateParam);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        calendarViewModel.getUiState().observe(getViewLifecycleOwner(), uiState ->
                binding.calendarView.setDate(
                        uiState.getDate()
                                .atStartOfDay(ZoneId.systemDefault())
                                .toEpochSecond() * 1000
        ));

        binding.calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
                    LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
                    calendarViewModel.setDate(date);
                    ((MainActivity) requireActivity()).navigateToHome(date);
        });
    }
}