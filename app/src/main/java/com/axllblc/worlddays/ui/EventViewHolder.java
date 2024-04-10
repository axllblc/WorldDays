package com.axllblc.worlddays.ui;

import static com.axllblc.worlddays.Utils.firstLetterToUppercase;
import static com.axllblc.worlddays.Utils.formatFullDate;

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
        String title = firstLetterToUppercase(event.getTitle());
        dayOfMonth.setText(Integer.toString(event.getMonthDay().getDayOfMonth()));
        eventTitle.setText(title);
        eventDate.setText(
                firstLetterToUppercase(formatFullDate(event.getOccurrenceForYear(year)))
        );

        card.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), DetailsActivity.class);
            i.putExtra(DetailsActivity.ARG_EVENT_ID, event.getId());
            v.getContext().startActivity(i);
        });
    }
}
