package com.axllblc.worlddays.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.axllblc.worlddays.DetailsActivity;
import com.axllblc.worlddays.R;
import com.axllblc.worlddays.data.Event;
import com.google.android.material.card.MaterialCardView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private final MaterialCardView card;
    private final TextView dayOfMonth;
    private final TextView eventTitle;
    private final TextView eventDate;

    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        card = itemView.findViewById(R.id.event_card_root);
        dayOfMonth = itemView.findViewById(R.id.day_of_month);
        eventTitle = itemView.findViewById(R.id.event_title);
        eventDate = itemView.findViewById(R.id.event_date);
    }

    @SuppressLint("SetTextI18n")
    public void setEvent(Event event, int year) {
        // Convert first character of event title to upper case
        String title = event.getTitle().substring(0, 1).toUpperCase()
                + event.getTitle().substring(1);
        dayOfMonth.setText(Integer.toString(event.getMonthDay().getDayOfMonth()));
        eventTitle.setText(title);
        eventDate.setText(event.getOccurrenceForYear(year)
                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
        );
        card.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), DetailsActivity.class);
            i.putExtra(DetailsActivity.ARG_EVENT_ID, event.getId());
            v.getContext().startActivity(i);
        });
    }
}
