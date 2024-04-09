package com.axllblc.worlddays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.details, menu);

        if (viewModel.getUiState().getValue().getEvent() != null) {
            // Share menu item
            MenuItem share = menu.findItem(R.id.share);
            share.setVisible(true);

            // Add to favorites / Remove from favorite menu items
            Boolean isFavorite = viewModel.getUiState().getValue().getIsFavorite();
            if (isFavorite != null) {
                MenuItem addToFavorites = menu.findItem(R.id.add_to_favorites);
                MenuItem removeFromFavorites = menu.findItem(R.id.remove_from_favorites);
                if (viewModel.getUiState().getValue().getIsFavorite()) {
                    addToFavorites.setVisible(false);
                    removeFromFavorites.setVisible(true);
                    removeFromFavorites.setOnMenuItemClickListener(item -> {
                        viewModel.setFavorite(false);
                        return true;
                    });
                } else {
                    addToFavorites.setVisible(true);
                    addToFavorites.setOnMenuItemClickListener(item -> {
                        viewModel.setFavorite(true);
                        return true;
                    });
                    removeFromFavorites.setVisible(false);
                }
            }
        }

        return true;
    }

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

            invalidateMenu();
        }
        if (uiState.getException() != null) {
            Snackbar.make(binding.getRoot(), "Something went wrong", Snackbar.LENGTH_SHORT)
                    .show();
            Log.e("err", "err", uiState.getException());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}