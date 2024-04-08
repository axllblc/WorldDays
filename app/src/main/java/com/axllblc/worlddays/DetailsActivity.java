package com.axllblc.worlddays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.axllblc.worlddays.data.Event;
import com.axllblc.worlddays.databinding.ActivityDetailsBinding;
import com.axllblc.worlddays.ui.viewmodel.DetailsUiState;
import com.axllblc.worlddays.ui.viewmodel.DetailsViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailsActivity extends AppCompatActivity {
    private DetailsViewModel viewModel;
    private ActivityDetailsBinding binding;
    public static final String ARG_EVENT_ID = "eventId";
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(ARG_EVENT_ID);

        viewModel.getUiState().observe(this, this::updateUi);
    }

    @Override
    protected void onStart() {
        super.onStart();

        viewModel.setEventId(Objects.requireNonNull(eventId));
    }

    private void updateUi(DetailsUiState uiState) {
        Event event = uiState.getEvent();
        if (event != null) {
            binding.eventDetailsTitle.setText(event.getTitle());

            binding.eventDetailsDate.setText(event.getNextOccurrence()
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            );

            binding.eventDetailsDateIn.setText(getString(
                    R.string.date_in,
                    LocalDate.now().until(event.getNextOccurrence(), ChronoUnit.DAYS)
            ));

            if (event.getInception() != null) {
                binding.eventDetailsInception.setText(getString(
                        R.string.since,
                        event.getInception().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                ));
                binding.eventDetailsInception.setVisibility(View.VISIBLE);
            }

            if (event.getWikipediaIntro() != null) {
                binding.eventDetailsIntro.setText(event.getWikipediaIntro());
                binding.eventDetailsIntro.setVisibility(View.VISIBLE);
            }

            if (event.getWikipediaURL() != null) {
                binding.eventDetailsOpenWikipedia.setVisibility(View.VISIBLE);
            }
        }
        if (uiState.getException() != null) {
            Snackbar.make(binding.getRoot(), "Something went wrong", Snackbar.LENGTH_SHORT)
                    .show();
            Log.e("err", "err", uiState.getException());
        }
    }
}