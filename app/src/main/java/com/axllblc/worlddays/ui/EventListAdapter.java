package com.axllblc.worlddays.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.axllblc.worlddays.R;
import com.axllblc.worlddays.data.Event;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<Event> events;
    private final int year;

    public EventListAdapter(List<Event> events, int year) {
        this.events = events;
        this.year = year;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.setEvent(events.get(position), year);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
