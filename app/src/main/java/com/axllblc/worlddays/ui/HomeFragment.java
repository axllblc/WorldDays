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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axllblc.worlddays.databinding.FragmentHomeBinding;
import com.axllblc.worlddays.ui.viewmodel.HomeUiState;
import com.axllblc.worlddays.ui.viewmodel.HomeViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.Optional;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to create an instance of this fragment.
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;

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

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        Optional<LocalDate> dateParam;
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                dateParam = Optional.ofNullable(
                        getArguments().getSerializable(ARG_DATE, LocalDate.class)
                );
            } else {
                dateParam = Optional.ofNullable(
                        (LocalDate) getArguments().getSerializable(ARG_DATE)
                );
            }
            viewModel.setDate(dateParam.orElse(LocalDate.now()));
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
        // Set LayoutManager for RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        binding.eventsList.setLayoutManager(layoutManager);

        // Refresh fragment content when UI state changes
        viewModel.getUiState().observe(getViewLifecycleOwner(), this::refreshFragment);
    }

    private void refreshFragment(HomeUiState uiState) {
        // Display error messages
        if (uiState.getException() != null) {
            Snackbar.make(requireView(), "Something went wrong", Snackbar.LENGTH_SHORT)
                    .show();
            viewModel.clearException();
        }

        // Display event list
        EventListAdapter adapter =
                new EventListAdapter(uiState.getEvents(), uiState.getDate().getYear());
        binding.eventsList.setAdapter(adapter);
    }
}