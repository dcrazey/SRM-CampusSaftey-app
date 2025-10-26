package com.srm_campussaftey;

// Correct R import
import com.srm_campussaftey.R;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location; // Correct Location import
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log; // <<<--- IMPORT ADDED
import android.view.View;
import android.widget.Button; // Correct Button import
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat; // Correct ActivityCompat import
import androidx.core.content.ContextCompat; // Correct ContextCompat import

// --- Import FusedLocationProviderClient and related classes ---
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
// --- End FusedLocationProviderClient Imports ---


import com.google.android.material.button.MaterialButton; // Correct MaterialButton import

import java.util.ArrayList; // <<<--- IMPORT ADDED
import java.util.List;
import java.util.Map; // <<<--- IMPORT ADDED (needed for ActivityResultContracts)

/*
 * This is the main screen of the application.
 * Handles permissions, SOS button, and navigation to other screens.
 */
public class MainActivity extends AppCompatActivity {

    // --- Declare UI elements at class level ---
    private Button sosButton; // Main SOS is a Button
    private MaterialButton contactsButton; // Use MaterialButton
    private MaterialButton feedButton; // Use MaterialButton
    // --- End UI element declaration ---

    // --- Declare FusedLocationProviderClient ---
    private FusedLocationProviderClient fusedLocationClient;
    // --- End FusedLocationProviderClient Declaration ---

    // Permission request launcher
    // Use Map<String, Boolean> for the result type
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> result) -> {
                // Use getOrDefault only if you are sure the key exists or handle null
                Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                Boolean smsGranted = result.get(Manifest.permission.SEND_SMS);

                // Check for null before using boolean value
                if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                    Log.i("Permissions", "Location permission granted.");
                    // You might want to trigger SOS again or enable location features
                } else {
                    Log.w("Permissions", "Location permission denied.");
                    Toast.makeText(this, "Location permission is needed for SOS with location.", Toast.LENGTH_LONG).show();
                }

                if (smsGranted != null && smsGranted) {
                    Log.i("Permissions", "SMS permission granted.");
                    // SMS feature is now usable
                } else {
                    Log.w("Permissions", "SMS permission denied.");
                    Toast.makeText(this, "SMS permission is required to send SOS alerts.", Toast.LENGTH_LONG).show();
                }
                // Check for Accessibility Service after handling SMS/Location
                checkAndPromptAccessibility();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Initialize FusedLocationProviderClient ---
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // --- End FusedLocationProviderClient Initialization ---


        // --- Initialize UI Elements ---
        sosButton = findViewById(R.id.sosButton);
        contactsButton = findViewById(R.id.contactsButton);
        feedButton = findViewById(R.id.feedButton);
        // --- End Initialize UI Elements ---


        // Basic null check (Important!)
        if (sosButton == null || contactsButton == null || feedButton == null) {
            Log.e("MainActivity", "Error finding essential views! Check layout file (activity_main.xml) for correct IDs.");
            Toast.makeText(this, "Critical Error initializing screen. Cannot find buttons.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity if views aren't found
            return;
        }


        // Check and request necessary permissions on startup
        checkAndRequestPermissions(); // This now also handles Accessibility prompt


        // --- Set Click Listeners ---
        sosButton.setOnClickListener(v -> {
            Log.d("MainActivity", "SOS Button Clicked");
            // Check permissions again just before sending
            if (hasSmsPermission() && hasLocationPermission()) {
                Toast.makeText(MainActivity.this, "SOS Activated! Getting location...", Toast.LENGTH_SHORT).show();
                // Trigger SOS with location attempt
                triggerSOS(true); // Request location
            } else {
                // If permissions were denied after initial grant, request again or show error
                Toast.makeText(MainActivity.this, "Permissions needed. Please grant SMS and Location access.", Toast.LENGTH_LONG).show();
                checkAndRequestPermissions(); // Re-request if needed
            }
        });

        contactsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            startActivity(intent);
        });

        feedButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
        });
        // --- End Click Listeners ---

    } // End of onCreate


    /**
     * Checks if SMS and Location permissions are granted. If not, requests them.
     * Also checks for Accessibility Service after permissions.
     */
    private void checkAndRequestPermissions() {
        String[] requiredPermissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, // Include coarse as an alternative
                Manifest.permission.SEND_SMS
        };

        // Filter out permissions that are already granted
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Don't request both fine and coarse if fine is needed
                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // If fine location is NOT granted, we still need to request *something*.
                    // If fine *is* already requested, skip coarse.
                    boolean fineAlreadyRequested = false;
                    for(String req : permissionsToRequest) {
                        if (req.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            fineAlreadyRequested = true;
                            break;
                        }
                    }
                    if(!fineAlreadyRequested) {
                        permissionsToRequest.add(permission); // Add coarse if fine isn't granted AND not already requested
                    }
                } else {
                    permissionsToRequest.add(permission); // Add fine or SMS if not granted
                }
            }
        }


        if (!permissionsToRequest.isEmpty()) {
            Log.i("Permissions", "Requesting permissions: " + permissionsToRequest);
            requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            Log.i("Permissions", "All required permissions already granted.");
            // Permissions granted, now check Accessibility Service
            checkAndPromptAccessibility();
        }
    }


    /**
     * Helper method to check if SMS permission is granted.
     */
    private boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Helper method to check if either Fine or Coarse Location permission is granted.
     */
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Initiates the SOS process by calling the SOSManager.
     * @param includeLocation True to attempt getting location, false otherwise.
     */
    private void triggerSOS(boolean includeLocation) {
        // We call the static method in SOSManager directly
        SOSManager.sendSOS(this, includeLocation); // Calls the refactored method
    }


    // --- Accessibility Service Check and Prompt ---

    /**
     * Checks if the Accessibility Service is enabled. If not, prompts the user.
     */
    private void checkAndPromptAccessibility() {
        if (!isAccessibilityServiceEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Shortcut Service")
                    .setMessage("To use the volume button SOS shortcut, please enable the Campus Safety accessibility service in your phone's settings.")
                    .setPositiveButton("Go to Settings", (dialog, which) -> {
                        // Intent to open Accessibility settings
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "Find 'Campus Safety' and turn it ON.", Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("Maybe Later", null)
                    .show();
        } else {
            Log.i("Accessibility", "Service already enabled.");
        }
    }

    /**
     * Checks if our specific Accessibility Service is enabled in system settings.
     * @return true if enabled, false otherwise.
     */
    private boolean isAccessibilityServiceEnabled() {
        int accessibilityEnabled = 0;
        // Construct the expected service name string
        // Make sure SOSAccessibilityService class exists and is correctly named
        String serviceName = SOSAccessibilityService.class.getCanonicalName();
        if (serviceName == null) {
            Log.e("Accessibility", "Could not get canonical name for SOSAccessibilityService");
            return false; // Cannot check if the class name is unavailable
        }
        final String service = getPackageName() + "/" + serviceName;

        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("Accessibility", "ACCESSIBILITY_ENABLED = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("Accessibility", "Error finding ACCESSIBILITY_ENABLED setting: " + e.getMessage());
            // Setting not found means it's effectively disabled for our check
            accessibilityEnabled = 0;
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("Accessibility", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.v("Accessibility", "ENABLED_ACCESSIBILITY_SERVICES = " + settingValue); // Log the raw value

            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("Accessibility", "Checking service :: " + accessibilityService + " | Expected :: " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("Accessibility", "Service match found - enabled!");
                        return true;
                    }
                }
                Log.w("Accessibility", "Enabled services found, but our service is not listed.");
            } else {
                Log.w("Accessibility", "Accessibility enabled, but ENABLED_ACCESSIBILITY_SERVICES string is null.");
            }
        } else {
            Log.v("Accessibility", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }


} // End of MainActivity

