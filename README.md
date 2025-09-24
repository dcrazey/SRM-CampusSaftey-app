Campus Safety App - A Native Android Application
A student project focused on enhancing safety for students on campus through a one-click SOS alert system. This repository contains the initial prototype (30% completion) of the application.

🚩 Project Overview
Students, especially those living in hostels or studying late at night, often face safety concerns ranging from harassment to medical emergencies. The Campus Safety App is designed to be a quick and reliable tool to bridge the gap between a student in distress and immediate help.

The core feature is a one-click Emergency SOS button that, upon activation, will automatically send an alert with the user's live GPS location to campus security, wardens, and a list of trusted contacts.

📱 Prototype Showcase (30% Milestone)
This initial version of the app establishes the foundational structure, user interface, and core logic connections.

(Replace this with a screenshot of your app running in the emulator)

What's Working in this Prototype:
Main User Interface: The app's main screen is fully designed using Android XML, featuring a clean layout with three primary buttons: SOS, Safe-Walk, and Incident Report.

Core Java Logic: The MainActivity.java file is set up to control the UI elements.

Interactive SOS Button: The SOS button is fully functional. When clicked, it displays a confirmation "Toast" message (SOS Activated! Alerting authorities...), demonstrating that the UI (XML) and the application logic (Java) are successfully linked.

Organized Project Structure: The project includes organized resource files for colors, styles (themes), and drawable assets, which is a best practice for scalable Android development.

🛠️ Technology Stack
Platform: Native Android

Language: Java

IDE: Android Studio

UI: XML (eXtensible Markup Language)

Build System: Gradle

🚀 How to Run This Project
To run this prototype on your machine, follow these steps:

Clone the repository:

git clone [https://github.com/your-username/campus-safety-app.git](https://github.com/your-username/campus-safety-app.git)

Open in Android Studio:

Launch Android Studio.

Select File > Open and navigate to the cloned project directory.

Sync Gradle:

Android Studio will automatically start syncing the project's Gradle files. Wait for it to complete.

Set up an Emulator:

Go to Tools > Device Manager.

Create a new virtual device (e.g., Pixel 6 with API 33).

Run the App:

Select the created emulator from the device dropdown menu.

Click the green play button (▶️) to build and run the application.

🗺️ Project Roadmap
This prototype represents the first 30% of the project. The next steps are:

[ ] (40%) Core SOS Functionality:

[ ] Request User Permissions (GPS Location, Send SMS).

[ ] Integrate Android's Location Services to fetch the device's live coordinates.

[ ] Implement SMS functionality to send the alert message and location to contacts.

[ ] (30%) Feature Finalization:

[ ] Develop the "Safe-Walk" feature for live location sharing with a friend.

[ ] Build the UI and backend logic for the "Incident Reporting" form.

[ ] Conduct thorough testing and debugging across different devices.

👥 Our Team
This project is a collaborative effort by:

Dito Dileep (RA2411026010050) - Lead Android Developer (App Building & Coding)

Cyan Benny (RA2411026010012) - Project Management & Presentation

Sujai Jaideep (RA2411026010008) - Research & Presentation

📄 License
This project is licensed under the MIT License - see the LICENSE.md file for details.
