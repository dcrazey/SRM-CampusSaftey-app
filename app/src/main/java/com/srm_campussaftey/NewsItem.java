package com.srm_campussaftey;

/*
 * This is a simple "Model" class, just like our Contact class.
 * Its only job is to hold the data for a single news/incident post.
 */
public class NewsItem {
    private String title;
    private String description;
    private long timestamp; // We'll store the time as a simple number (milliseconds)

    // A blank constructor is needed for saving/loading with some libraries (like Firebase)
    public NewsItem() {
    }

    // Constructor: Called when we create a new NewsItem object
    public NewsItem(String title, String description, long timestamp) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    // "Getter" methods
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
