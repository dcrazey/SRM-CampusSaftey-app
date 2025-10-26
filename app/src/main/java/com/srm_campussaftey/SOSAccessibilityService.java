package com.srm_campussaftey;

// Correct R import
import com.srm_campussaftey.R;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context; // Import Context
import android.content.Intent;
import android.os.Handler;
import android.os.Looper; // Corrected from Loos
import android.util.Log; // Import Log
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;


/*
 * This is an Accessibility Service, which is a special background service.
 * We are using it to listen for hardware key presses (like the Volume Down button)
 * even when the app is closed.
 *
 * IMPORTANT: This service must be manually enabled by the user in the phone's
 * Accessibility Settings for it to work. MainActivity prompts the user to do this.
 */
public class SOSAccessibilityService extends AccessibilityService {

    private static final String TAG = "SOSAccessibilityService";
    private static final int PRESS_THRESHOLD = 3; // Number of presses needed
    private static final long TIME_WINDOW_MS = 2000; // Time window in milliseconds (2 seconds)

    private int volumeDownPressCount = 0;
    private long lastVolumeDownPressTime = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable resetCounterRunnable = new Runnable() {
        @Override
        public void run() {
            volumeDownPressCount = 0;
            Log.d(TAG, "Volume down press counter reset due to timeout.");
        }
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // We don't need to handle general accessibility events for this feature.
    }

    @Override
    public void onInterrupt() {
        // Called when the system wants to interrupt the feedback your service is providing.
        Log.w(TAG, "Service interrupted.");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        // We only care about KEY DOWN events for the Volume Down button
        if (action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            long currentTime = System.currentTimeMillis();
            Log.d(TAG, "Volume Down Pressed. Count: " + (volumeDownPressCount + 1));

            // Remove any pending reset runnable
            handler.removeCallbacks(resetCounterRunnable);

            // Check if this press is within the time window of the previous press
            if (currentTime - lastVolumeDownPressTime <= TIME_WINDOW_MS) {
                volumeDownPressCount++;
            } else {
                // First press or outside time window, reset count to 1
                volumeDownPressCount = 1;
            }

            lastVolumeDownPressTime = currentTime;

            // Check if threshold is met
            if (volumeDownPressCount >= PRESS_THRESHOLD) {
                Log.i(TAG, "SOS Triggered via Volume Down!");
                Toast.makeText(this, "SOS Triggered!", Toast.LENGTH_SHORT).show();

                // Reset count immediately after triggering
                volumeDownPressCount = 0;

                // --- Trigger the SOS action ---
                // Get the application context for the SOSManager
                Context context = getApplicationContext();
                // Send SOS without location since we are in the background service
                SOSManager.sendSOS(context, false); // Pass context and false for includeLocation <<<--- FIXED THIS CALL

                // --- Important: Consume the event ---
                // Return true to prevent the volume from actually changing
                return true;

            } else {
                // If threshold not met, schedule a reset after the time window
                handler.postDelayed(resetCounterRunnable, TIME_WINDOW_MS);
            }

            // Return false if we haven't triggered SOS, allowing volume to change normally.
            // If we returned true above, the volume change is blocked.
            return false;
        }

        // Let the system handle other key events
        return super.onKeyEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "Accessibility Service Connected.");
        // Configuration is done via XML (accessibility_service_config.xml)
        // Ensure you have android:canRetrieveWindowContent="false" if you don't need screen content
        // Ensure you have android:accessibilityFlags="flagRequestFilterKeyEvents"
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up handler callbacks if the service is destroyed
        handler.removeCallbacks(resetCounterRunnable);
        Log.i(TAG, "Accessibility Service Destroyed.");
    }
}

