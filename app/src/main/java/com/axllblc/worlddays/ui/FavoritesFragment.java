package com.axllblc.worlddays.ui;

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

import com.axllblc.worlddays.databinding.FragmentFavoritesBinding;
import com.axllblc.worlddays.ui.viewmodel.FavoritesUiState;
import com.axllblc.worlddays.ui.viewmodel.FavoritesViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoritesFragment extends Fragment {
    private FavoritesViewModel viewModel;
    private FragmentFavoritesBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
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

    private void refreshFragment(FavoritesUiState uiState) {
        // Display error messages
        if (uiState.getException() != null) {
            Snackbar.make(requireView(), "Something went wrong", Snackbar.LENGTH_SHORT)
                    .show();
            viewModel.clearException();
        }

        // Display event list
        EventListAdapter adapter =
                new EventListAdapter(uiState.getEvents(), LocalDate.now().getYear());
        binding.eventsList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.fetch();
    }
}