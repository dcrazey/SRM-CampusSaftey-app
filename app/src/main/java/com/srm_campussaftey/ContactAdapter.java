package com.srm_campussaftey;

// Correct R import

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/*
 * An Adapter is a "bridge" between your data (the List<Contact>) and the
 * UI element that displays it (the RecyclerView).
 * It inflates the layout for each row and binds the data to the views.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    // --- Declare variables at class level ---
    private final Context context; // Need context for layout inflater
    private final List<Contact> contactList;
    private final OnDeleteClickListener deleteClickListener; // Listener for delete clicks
    // --- End variable declaration ---


    // Interface for handling delete clicks back in the Activity
    public interface OnDeleteClickListener {
        void onDeleteClick(Contact contact);
    }

    /**
     * Constructor for the adapter.
     * @param context The activity context.
     * @param contactList The list of contacts to display.
     * @param deleteClickListener The listener to notify when a delete button is clicked.
     */
    public ContactAdapter(Context context, List<Contact> contactList, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.contactList = contactList;
        this.deleteClickListener = deleteClickListener;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder (a new row).
     * We inflate our list_item_contact.xml layout here.
     */
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single row
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    /**
     * Called when RecyclerView wants to display data at a specific position.
     * We get the Contact object and set the text in the ViewHolder's views.
     */
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        // Get the contact for the current row
        Contact currentContact = contactList.get(position);

        // Set the text for the name and phone TextViews
        holder.nameTextView.setText(currentContact.getName());
        holder.phoneTextView.setText(currentContact.getPhone());

        // Set an OnClickListener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            // When clicked, call the listener's method, passing the contact to delete
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(currentContact);
            }
        });
    }

    /**
     * Tells the RecyclerView how many items are in our list.
     */
    @Override
    public int getItemCount() {
        // Return 0 if the list is null to prevent crashes
        return contactList != null ? contactList.size() : 0;
    }


    /**
     * The ViewHolder holds references to the views within each row layout.
     * This avoids repeatedly calling findViewById(), which is inefficient.
     */
    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        // --- Declare UI elements within the ViewHolder ---
        public TextView nameTextView;
        public TextView phoneTextView;
        public ImageView deleteButton; // Changed from ImageButton to ImageView to match XML
        // --- End UI element declaration ---

        // Constructor for the ViewHolder
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views within the inflated row layout (list_item_contact.xml)
            // Use the CORRECT IDs from the XML file
            nameTextView = itemView.findViewById(R.id.contactNameText);
            phoneTextView = itemView.findViewById(R.id.contactPhoneText);
            deleteButton = itemView.findViewById(R.id.deleteContactButton);
        }
    }
}

