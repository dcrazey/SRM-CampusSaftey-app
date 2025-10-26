package com.srm_campussaftey;

// Correct R import

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * This is the "brain" for our Campus Feed screen (activity_feed.xml).
 * It manages loading, displaying, and adding new incident posts.
 */
public class FeedActivity extends AppCompatActivity {

    // --- Constants for saving/loading data ---
    public static final String PREFS_NAME = "CampusSafetyPrefs"; // Same prefs file as Contacts
    public static final String NEWS_KEY = "CampusNewsFeed";
    // --- End of Constants ---

    // --- Declare UI elements at class level ---
    private RecyclerView newsRecyclerView;
    private FloatingActionButton addNewsButton; // Correct type
    private ImageButton backButton; // Correct type
    // --- End UI element declaration ---

    private List<NewsItem> newsList;
    private NewsAdapter newsAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed); // Links to the correct XML

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // --- Load UI elements from XML ---
        // Use the CORRECT IDs from activity_feed.xml
        newsRecyclerView = findViewById(R.id.feedRecyclerView);
        addNewsButton = findViewById(R.id.addPostButton);
        backButton = findViewById(R.id.backButton); // Ensure this ID exists in activity_feed.xml
        // --- End Finding UI elements ---

        // Basic null check
        if (newsRecyclerView == null || addNewsButton == null || backButton == null) {
            Log.e("FeedActivity", "Error finding essential views! Check layout file (activity_feed.xml) for correct IDs.");
            Toast.makeText(this, "Error initializing feed screen.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if views aren't found
            return;
        }

        // Load saved news items
        loadNews();

        // --- Setup RecyclerView ---
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Pass "this" (the Context) and the List to the adapter constructor
        newsAdapter = new NewsAdapter(this, newsList);
        newsRecyclerView.setAdapter(newsAdapter);
        // --- End RecyclerView Setup ---

        // --- Set Click Listeners ---
        addNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNewsDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close this screen
            }
        });
        // --- End Set Click Listeners ---
    }

    /**
     * Loads the list of news items from SharedPreferences.
     */
    private void loadNews() {
        String jsonNews = sharedPreferences.getString(NEWS_KEY, null);
        Gson gson = new Gson();

        if (jsonNews == null) {
            newsList = new ArrayList<>();
            // Optional: Add a default welcome message if the list is empty
            // newsList.add(new NewsItem("Welcome", "No incidents reported yet.", System.currentTimeMillis()));
        } else {
            Type type = new TypeToken<ArrayList<NewsItem>>() {}.getType();
            newsList = gson.fromJson(jsonNews, type);
            // Sort news by timestamp, newest first
            Collections.sort(newsList, new Comparator<NewsItem>() {
                @Override
                public int compare(NewsItem o1, NewsItem o2) {
                    return Long.compare(o2.getTimestamp(), o1.getTimestamp()); // Descending order
                }
            });
        }
    }

    /**
     * Saves the entire (modified) news list back to SharedPreferences.
     */
    private void saveNews() {
        // Sort before saving to maintain order (optional, but good practice)
        Collections.sort(newsList, new Comparator<NewsItem>() {
            @Override
            public int compare(NewsItem o1, NewsItem o2) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp()); // Descending order
            }
        });

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonNews = gson.toJson(newsList);
        editor.putString(NEWS_KEY, jsonNews);
        editor.apply();
    }

    /**
     * Shows a pop-up dialog for the user to add a new incident report.
     */
    private void showAddNewsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_post, null); // Use the dialog layout

        // Find views within the dialog layout using the CORRECT IDs
        final EditText titleEditText = dialogView.findViewById(R.id.postTitleEditText); // Ensure these IDs exist
        final EditText descriptionEditText = dialogView.findViewById(R.id.postDescriptionEditText); // Ensure these IDs exist

        builder.setView(dialogView)
                .setTitle("Report New Incident")
                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String title = titleEditText.getText().toString().trim();
                        String description = descriptionEditText.getText().toString().trim();

                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
                            addNewPost(title, description);
                        } else {
                            Toast.makeText(FeedActivity.this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Adds a new post to the list, saves it, and updates the RecyclerView.
     */
    private void addNewPost(String title, String description) {
        NewsItem newItem = new NewsItem(title, description, System.currentTimeMillis());
        // We add to the beginning, but our layout is reversed, so it appears at the top
        // newsList.add(0, newItem); // Add to the beginning for newest first effect
        // OR add to the end and rely on sorting during load/save
        newsList.add(newItem);
        saveNews(); // Save the updated list (which also sorts it)

        // Reload and resort the list to ensure order is correct immediately
        loadNews();

        // Notify the adapter that the data set has changed completely
        newsAdapter.notifyDataSetChanged(); // Simple way to refresh after sorting

        // Optionally scroll to the top
        if (newsList.size() > 0) {
            newsRecyclerView.scrollToPosition(0);
        }


        Toast.makeText(this, "Incident posted", Toast.LENGTH_SHORT).show();
    }
}

