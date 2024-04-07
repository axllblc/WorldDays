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

import com.axllblc.worlddays.databinding.FragmentHomeBinding;
import com.axllblc.worlddays.ui.viewmodel.CalendarViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to create an instance of this fragment.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private CalendarViewModel calendarViewModel;

    private FragmentHomeBinding binding;

    // the fragment initialization parameters
    public static final String ARG_DATE = "date";


    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @param date Date to use in HomeFragment
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(@NonNull LocalDate date) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of this fragment, with default parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        calendarViewModel.getUiState().observe(getViewLifecycleOwner(), uiState -> {
            binding.fragmentHomeTextview.setText(
                    uiState.getEvents().stream().limit(1).collect(Collectors.toList()).toString()
            );

            // Display error messages
            if (uiState.getException() != null) {
                Snackbar.make(requireView(), "Something went wrong", Snackbar.LENGTH_SHORT)
                        .show();
                calendarViewModel.clearException();
            }
        });

        binding.test.setOnClickListener(v ->
                calendarViewModel.fetchEventsForMonth(calendarViewModel.getUiState().getValue()
                        .getDate().getMonth()
                )
        );
    }
}