package com.srm_campussaftey;

// Correct R import
import com.srm_campussaftey.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Import Log
import android.view.View;
import android.widget.Button; // Correct import
import android.widget.EditText;
import android.widget.ImageButton; // Correct import
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
 * This is the "brain" for our Contacts screen (activity_contacts.xml).
 * It manages loading, displaying, adding, and deleting contacts.
 */
public class ContactsActivity extends AppCompatActivity {

    // --- Constants for saving/loading data ---
    public static final String PREFS_NAME = "CampusSafetyPrefs";
    public static final String CONTACTS_KEY = "EmergencyContacts";
    // --- End of Constants ---

    // --- Declare UI elements at class level ---
    private RecyclerView contactsRecyclerView;
    private EditText nameEditText;
    private EditText phoneEditText;
    private Button addButton;
    private ImageButton backButton; // Declaration is correct
    // --- End UI element declaration ---

    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // --- Initialize UI Elements ---
        // Find UI elements and assign them, WITH EXPLICIT CASTS
        contactsRecyclerView = (RecyclerView) findViewById(R.id.contactsRecyclerView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        // Use the CORRECT ID from the XML file
        addButton = (Button) findViewById(R.id.addContactButton);
        backButton = (ImageButton) findViewById(R.id.backButton); // This ID must exist in activity_contacts.xml
        // --- End Finding UI elements ---

        // Basic null check after findViewById (optional but good practice)
        // Note: backButton will be null here until we add it to the XML
        if (contactsRecyclerView == null || nameEditText == null || phoneEditText == null || addButton == null) {
            // Removed backButton check for now
            Log.e("ContactsActivity", "Error finding essential views! Check layout file (activity_contacts.xml) for correct IDs (contactsRecyclerView, nameEditText, phoneEditText, addContactButton).");
            Toast.makeText(this, "Error initializing screen.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if views aren't found
            return;
        }


        loadContacts();
        setupRecyclerView();
        setupClickListeners(); // Setup listeners after checking essential views
    }

    /**
     * Sets up the RecyclerView with its adapter.
     */
    private void setupRecyclerView() {
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create the adapter. We pass "this" to listen for delete clicks.
        contactAdapter = new ContactAdapter(this, contactList, new ContactAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Contact contact) {
                // This code runs when the user clicks a "delete" button in the adapter
                deleteContact(contact);
            }
        });
        contactsRecyclerView.setAdapter(contactAdapter);
    }

    /**
     * Sets up the click listeners for the buttons.
     */
    private void setupClickListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        // Add null check for backButton before setting listener
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // finish() simply closes this screen and goes back to the previous one
                    finish();
                }
            });
        } else {
            Log.w("ContactsActivity", "Back button not found in layout (R.id.backButton missing in activity_contacts.xml)");
            // Optionally, disable or hide functionality that depends on the back button
        }
    }

    /**
     * Loads the list of contacts from SharedPreferences.
     */
    private void loadContacts() {
        String jsonContacts = sharedPreferences.getString(CONTACTS_KEY, null);
        Gson gson = new Gson();

        if (jsonContacts == null) {
            // No contacts saved yet, create a new empty list
            contactList = new ArrayList<>();
        } else {
            // Contacts exist, use Gson to turn the JSON string back into a List
            Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
            try {
                contactList = gson.fromJson(jsonContacts, type);
                // Ensure list is not null after parsing
                if (contactList == null) {
                    contactList = new ArrayList<>();
                    Log.w("ContactsActivity", "Parsed contact list was null, initialized new list.");
                }
            } catch (Exception e) {
                // Handle potential JSON parsing errors
                contactList = new ArrayList<>();
                Log.e("ContactsActivity", "Error parsing contacts JSON", e);
                Toast.makeText(this, "Error loading contacts.", Toast.LENGTH_SHORT).show();
                // Optionally clear corrupted data:
                // sharedPreferences.edit().remove(CONTACTS_KEY).apply();
            }
        }
        Log.d("ContactsActivity", "Loaded " + contactList.size() + " contacts.");
    }

    /**
     * Saves the entire (modified) contact list back to SharedPreferences.
     */
    private void saveContacts() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        // Use Gson to turn our List<Contact> into a single JSON string
        String jsonContacts = gson.toJson(contactList);
        // Save that string
        editor.putString(CONTACTS_KEY, jsonContacts);
        editor.apply();
        Log.d("ContactsActivity", "Saved " + contactList.size() + " contacts.");
    }

    /**
     * Called when the "Add" button is clicked.
     */
    private void addContact() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // --- Simple Validation ---
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        // Basic phone number format check (optional, can be improved)
        if (!phone.matches("\\+?[0-9\\s-]+")) { // Allows digits, spaces, hyphens, optional leading +
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Add to list and save ---
        Contact newContact = new Contact(name, phone);
        contactList.add(newContact);
        saveContacts();

        // --- Update the UI ---
        // Notify the adapter that a new item was added at the end of the list
        if(contactAdapter != null) { // Add null check for adapter
            contactAdapter.notifyItemInserted(contactList.size() - 1);
            // Scroll to the newly added item (optional)
            contactsRecyclerView.smoothScrollToPosition(contactList.size() - 1);
        }
        // Clear the text boxes
        nameEditText.setText("");
        phoneEditText.setText("");
        Toast.makeText(this, "Contact added", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the "delete" button in the adapter is clicked.
     */
    private void deleteContact(Contact contact) {
        // Find the position of the contact in the list
        int position = -1;
        for(int i = 0; i < contactList.size(); i++){
            // Compare content, not just object reference
            if(contactList.get(i).getName().equals(contact.getName()) && contactList.get(i).getPhone().equals(contact.getPhone())){
                position = i;
                break;
            }
        }

        if (position != -1) {
            // Remove the contact from the list
            contactList.remove(position);
            // Save the new, smaller list
            saveContacts();
            // Notify the adapter that an item was removed at that position
            if(contactAdapter != null) { // Add null check for adapter
                contactAdapter.notifyItemRemoved(position);
                // Optional: Notify for range change if positions shift
                contactAdapter.notifyItemRangeChanged(position, contactList.size());
            }
            Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("ContactsActivity", "Could not find contact to delete: " + contact.getName());
            // Maybe refresh the list if contact wasn't found?
            // loadContacts();
            // contactAdapter.notifyDataSetChanged();
        }
    }
}

