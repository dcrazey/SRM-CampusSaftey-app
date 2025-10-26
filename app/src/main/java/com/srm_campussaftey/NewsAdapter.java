package com.srm_campussaftey;

// Correct R import

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * An Adapter is a "bridge" between your data (the List<NewsItem>) and the
 * UI element that displays it (the RecyclerView).
 * - It inflates the layout for each row and binds the data to the views.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final Context context; // Need context for LayoutInflater
    private final List<NewsItem> newsList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());

    // Constructor updated to accept Context
    public NewsAdapter(Context context, List<NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    // Called when RecyclerView needs a new ViewHolder (a new row layout)
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate (create) the XML layout for a single news item row
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_news, parent, false);
        return new NewsViewHolder(view);
    }

    // Called when RecyclerView wants to display data at a specific position (row)
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        // Get the data for the current row
        NewsItem currentItem = newsList.get(position);

        // Bind the data to the views inside the ViewHolder
        holder.titleTextView.setText(currentItem.getTitle());
        holder.descriptionTextView.setText(currentItem.getDescription());
        holder.timestampTextView.setText(dateFormat.format(new Date(currentItem.getTimestamp())));
    }

    // Called by RecyclerView to get the total number of items in the list
    @Override
    public int getItemCount() {
        return newsList.size();
    }

    /*
     * The ViewHolder holds references to the individual views within a single row layout.
     * This avoids repeatedly calling findViewById() which is inefficient.
     */
    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        // Declare the views within the row layout
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView timestampTextView;

        // Constructor for the ViewHolder
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views by their IDs in list_item_news.xml (USING CORRECT IDs NOW)
            titleTextView = itemView.findViewById(R.id.newsTitle); // Ensure these IDs exist
            descriptionTextView = itemView.findViewById(R.id.newsDescription); // Ensure these IDs exist
            timestampTextView = itemView.findViewById(R.id.newsTimestamp); // Ensure these IDs exist
        }
    }
}

