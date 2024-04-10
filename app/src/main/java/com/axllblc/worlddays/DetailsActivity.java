package com.axllblc.worlddays;

import static com.axllblc.worlddays.Utils.firstLetterToUppercase;
import static com.axllblc.worlddays.Utils.formatFullDate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.ZoneId;
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

    private static final String TAG = "DetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Get event ID
        Intent intent = getIntent();
        eventId = intent.getStringExtra(ARG_EVENT_ID);

        viewModel.getUiState().observe(this, this::updateUi);

        // Register BroadcastReceiver
        IntentFilter timeIntentFilter = new IntentFilter();
        timeIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        timeIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(new TimeBroadcastReceiver(), timeIntentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        viewModel.setEventId(Objects.requireNonNull(eventId));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.details, menu);

        Event event = Objects.requireNonNull(viewModel.getUiState().getValue()).getEvent();
        if (event != null) {
            // Share menu item
            MenuItem share = menu.findItem(R.id.share);
            share.setVisible(true);
            share.setOnMenuItemClickListener(item -> {
                Intent i = createShareIntent(event);
                startActivity(Intent.createChooser(
                        i, getString(R.string.share_event)
                ));
                return true;
            });

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


    // 👇 UI

    private void updateUi(DetailsUiState uiState) {
        Event event = uiState.getEvent();
        if (event != null) {
            binding.progressIndicator.setVisibility(View.GONE);

            binding.eventDetailsTitle.setText(firstLetterToUppercase(event.getTitle()));

            binding.eventDetailsDate.setText(
                    firstLetterToUppercase(formatFullDate(event.getNextOccurrence()))
            );

            setDateInText(event);

            View.OnClickListener dateCardClickListener = v -> {
                Intent i = createCalendarIntent(event);
                startActivity(i);
            };
            binding.eventDetailsDateCard.setVisibility(View.VISIBLE);
            binding.eventDetailsDateCard.setOnClickListener(dateCardClickListener);
            binding.eventDetailsAddToCalendar.setOnClickListener(dateCardClickListener);

            if (event.getInception() != null) {
                binding.eventDetailsInception.setText(getString(
                        R.string.since,
                        formatFullDate(event.getInception())
                ));
                binding.eventDetailsInception.setVisibility(View.VISIBLE);
            }

            if (event.getWikipediaIntro() != null) {
                binding.eventDetailsIntro.setText(event.getWikipediaIntro());
                binding.eventDetailsIntro.setVisibility(View.VISIBLE);
            }

            if (event.getWikipediaURL() != null) {
                binding.eventDetailsOpenWikipedia.setVisibility(View.VISIBLE);
                binding.eventDetailsOpenWikipedia.setOnClickListener(v -> {
                    Intent i = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(event.getWikipediaURL())
                    );
                    startActivity(i);
                });
            }

            invalidateMenu();
        }
        if (uiState.getException() != null) {
            Log.e(TAG, "Error", uiState.getException());
            new MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.something_went_wrong)
                    .setPositiveButton(R.string.ok, (dialog, which) -> finish())
                    .show();
        }
    }

    private void setDateInText(Event event) {
        binding.eventDetailsDateIn.setText(getString(
                R.string.date_in,
                LocalDate.now().until(event.getNextOccurrence(), ChronoUnit.DAYS)
        ));
    }


    // 👇 Intents

    private Intent createCalendarIntent(Event event) {
        long dateInMillis = event.getNextOccurrence()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond() * 1000;

        return new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, event.getTitle())
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateInMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dateInMillis + 1);
    }

    private Intent createShareIntent(Event event) {
        return new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.save_the_date))
                .putExtra(Intent.EXTRA_TEXT, getString(
                        R.string.share_message,
                        event.getTitle(),
                        event.getNextOccurrence()
                                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
                ));
    }


    /**
     * {@link BroadcastReceiver} that updates the {@code binding.eventDetailsDateIn} TextView on
     * time tick or when the system date changes.
     */
    private class TimeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Event event = Objects.requireNonNull(viewModel.getUiState().getValue()).getEvent();
            if (event != null) {
                setDateInText(event);
            }
        }
    }
}