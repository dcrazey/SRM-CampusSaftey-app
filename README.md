SRM Campus Safety App

A native Android application developed for the 21CSC203P - Advanced Programming Practice course at SRM Institute of Science & Technology.

This app provides students with essential safety tools, including a one-click SOS alert with GPS location sent via SMS, emergency contact management, a local campus incident feed, and a discreet hardware button shortcut for emergencies.

Team Members:

DITO DILEEP [RA2411026010050] - Lead Developer

SUJAI JAIDEEP [RA2411026010008] - Presentation & Documentation

CYAN BENNY [RA2411026010012] - Testing & Portfolio Management

Supervisor: Dr. M. Salomi

Features

One-Click SOS: Prominent SOS button on the main screen. Fetches current GPS location and sends an SMS alert (including a Google Maps link) to designated emergency contacts.

Emergency Contacts: Users can add, view, and delete trusted contacts (name and phone number) within the app. Contacts are saved locally on the device.

Campus Feed: A simple feed where users can post incident reports (title and description) and view posts made on their device. Data is stored locally using SharedPreferences.

Volume Button Shortcut: Utilizes Android's Accessibility Service (requires user permission) to detect three quick presses of the Volume Down button. Triggers an SOS SMS alert without location for faster, discreet activation.

Permissions Handling: Gracefully requests necessary permissions (Fine Location, Send SMS) on startup and guides the user if permissions are denied.

Accessibility Service Prompt: Guides the user to enable the required Accessibility Service in system settings to activate the volume button shortcut.

Clean UI: Minimalistic and professional user interface designed for ease of use.

Technology Stack

Language: Java

IDE: Android Studio

UI: XML Layouts, Material Components

Platform: Native Android

APIs:

Android Location Services (FusedLocationProviderClient)

Android Telephony (SmsManager)

Android Accessibility Service

Libraries:

Google Play Services Location (com.google.android.gms:play-services-location)

Gson (com.google.code.gson:gson)

AndroidX (AppCompat, RecyclerView, Material)

Storage: SharedPreferences (for contacts and feed posts)

Build System: Gradle

How to Build and Run

Clone the Repository:

git clone [https://github.com/dcrazey/SRM-CampusSaftey-app.git](https://github.com/dcrazey/SRM-CampusSaftey-app.git)


Open in Android Studio: Open the cloned project folder in the latest stable version of Android Studio.

Sync Gradle: Allow Android Studio to sync the project and download dependencies.

Connect Device/Emulator: Connect a physical Android device (recommended for testing SMS) or start an emulator.

Run the App: Click the Run button (▶️).

Grant Permissions: When the app starts, it will request Location and SMS permissions. You must grant these for the SOS features to work.

Enable Accessibility Service:

The app will prompt you to enable the Accessibility Service for the volume button shortcut.

Click "Go to Settings".

In your device's Accessibility settings, find "Campus Safety" (it might be under "Downloaded apps" or similar).

Turn the service ON and confirm any system warnings. This is required for the volume shortcut.

Add Contacts: Go to "My Contacts" within the app to add emergency numbers.

Test:

Press the main SOS button (requires permissions).

Press Volume Down three times quickly (requires Accessibility Service enabled).

Post an item to the Campus Feed.

Future Scope

Integrate Firebase Firestore for a shared Campus Feed and cloud backup of contacts.

Implement a "Safe-Walk" feature with real-time location sharing (using Firebase).

Add user authentication (Firebase Auth).

Integrate with official campus security systems via API (if available).
