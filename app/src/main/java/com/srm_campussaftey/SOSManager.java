package com.srm_campussaftey;

// Correct R import

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/*
 * Manages the core SOS logic: getting location and sending SMS alerts.
 */
public class SOSManager {

    private static final String TAG = "SOSManager"; // Tag for logging

    // --- Variables for Location ---
    private static FusedLocationProviderClient fusedLocationClient;
    private static LocationCallback locationCallback;
    private static Location lastLocation; // Store the last obtained location
    // --- End Location Variables ---


    /**
     * Sends the SOS SMS message to all saved emergency contacts.
     * Attempts to get current location if includeLocation is true.
     *
     * @param context         The application context.
     * @param includeLocation If true, attempts to get current location before sending.
     * If false, sends immediately without location (e.g., for shortcut).
     */
    public static void sendSOS(Context context, boolean includeLocation) {
        Log.d(TAG, "sendSOS called. includeLocation: " + includeLocation);

        // Initialize FusedLocationProviderClient if it hasn't been already
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        }

        if (includeLocation) {
            // --- Attempt to get current location ---
            Log.d(TAG, "Attempting to get current location...");
            // Check location permission before requesting updates
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Log.w(TAG, "Location permission not granted. Cannot get location for SOS.");
                Toast.makeText(context, "Location permission needed for SOS.", Toast.LENGTH_SHORT).show();
                // Send SMS without location as a fallback
                sendSmsMessage(context, loadContacts(context), null);
                return;
            }

            // Define location request parameters
            // PRIORITY_HIGH_ACCURACY is preferred for emergencies
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000) // Interval 5 seconds
                    .setMinUpdateIntervalMillis(1000) // Fastest update 1 second
                    .setMaxUpdates(1) // Only need one update
                    .build();


            // Define the callback to handle location updates
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    fusedLocationClient.removeLocationUpdates(locationCallback); // Stop listening after getting the location
                    if (locationResult.getLastLocation() != null) {
                        lastLocation = locationResult.getLastLocation();
                        Log.i(TAG, "Current location obtained: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
                        sendSmsMessage(context, loadContacts(context), lastLocation);
                    } else {
                        Log.w(TAG, "Failed to get current location (result was null). Sending without location.");
                        sendSmsMessage(context, loadContacts(context), null); // Send without location if fetching failed
                    }
                }
            };

            // Request location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            Log.d(TAG, "Location updates requested...");

        } else {
            // --- Send immediately without location (for shortcut) ---
            Log.d(TAG, "Sending SOS immediately without location.");
            sendSmsMessage(context, loadContacts(context), null);
        }
    }


    /**
     * Loads the list of emergency contacts from SharedPreferences.
     * @param context The application context.
     * @return List of Contact objects. Returns an empty list if none saved or error occurs.
     */
    private static List<Contact> loadContacts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ContactsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String jsonContacts = sharedPreferences.getString(ContactsActivity.CONTACTS_KEY, null);
        Gson gson = new Gson();
        List<Contact> contacts = new ArrayList<>(); // Default to empty list

        if (jsonContacts != null) {
            try {
                Type type = new TypeToken<ArrayList<Contact>>() {}.getType();
                contacts = gson.fromJson(jsonContacts, type);
                if (contacts == null) { // Handle case where JSON might be invalid "null"
                    contacts = new ArrayList<>();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing contacts JSON: " + e.getMessage());
                contacts = new ArrayList<>(); // Reset to empty list on error
            }
        }
        Log.d(TAG, "Loaded " + contacts.size() + " contacts.");
        return contacts;
    }

    /**
     * Constructs and sends the SMS message to the list of contacts.
     * @param context The application context.
     * @param contacts List of contacts to send to.
     * @param location The current location (can be null).
     */
    private static void sendSmsMessage(Context context, List<Contact> contacts, Location location) {
        if (contacts == null || contacts.isEmpty()) {
            Log.w(TAG, "No emergency contacts found. Cannot send SOS.");
            Toast.makeText(context, "No emergency contacts added.", Toast.LENGTH_LONG).show();
            return;
        }

        // --- Construct the Message ---
        String message = "EMERGENCY SOS! I need help.";
        if (location != null) {
            // If we have a location, add a Google Maps link
            message += " My approximate location is: "
                    + "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
        } else {
            message += " My location is unknown.";
        }
        // --- End of Message Construction ---


        // --- Send the SMS ---
        // Get the default Android SMS Manager
        SmsManager smsManager = SmsManager.getDefault();
        int contactsSent = 0;

        // Loop through every contact and send them the SMS
        for (Contact contact : contacts) {
            try {
                smsManager.sendTextMessage(contact.getPhone(), null, message, null, null);
                contactsSent++;
                Log.i(TAG, "SOS SMS sent to: " + contact.getName() + " (" + contact.getPhone() + ")");
            } catch (Exception e) {
                // Handle error (e.g., invalid phone number)
                Log.e(TAG, "Could not send SMS to " + contact.getName() + ": " + e.getMessage());
                Toast.makeText(context, "Could not send to " + contact.getName(), Toast.LENGTH_SHORT).show();
            }
        }
        // --- End Sending SMS ---

        if (contactsSent > 0) {
            Toast.makeText(context, "SOS sent to " + contactsSent + " contact(s).", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to send SOS to any contacts.", Toast.LENGTH_LONG).show();
        }

    } // End sendSmsMessage

} // End SOSManager class

