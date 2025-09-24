// CORRECTED: The package name now includes the underscore to match your project setup.
package com.srm_campussaftey;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Declare the buttons so we can use them in our code
    Button sosButton;
    Button safeWalkButton;
    Button reportIncidentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This line connects our Java code to the XML layout file
        setContentView(R.layout.activity_main);

        // Find the buttons from the layout file by their ID and assign them to our variables
        sosButton = findViewById(R.id.sosButton);
        safeWalkButton = findViewById(R.id.safeWalkButton);
        reportIncidentButton = findViewById(R.id.reportIncidentButton);

        // --- This is the core logic for  30% demo ---
        // Set a "click listener" on the SOS button. This code runs when the button is tapped.
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, just show a pop-up message (a "Toast") to confirm the button was pressed.
                // In the future, this is where we will add the code to get location and send alerts.
                Toast.makeText(MainActivity.this, "SOS Activated! Alerting authorities...", Toast.LENGTH_LONG).show();
            }
        });

        // We can also add listeners for the other buttons to show they are acknowledged
        safeWalkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Safe-Walk feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        reportIncidentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Incident Reporting feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
