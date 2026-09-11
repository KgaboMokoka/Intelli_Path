package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    // Dashboard cards
    private CardView cardBaseline;
    private CardView cardRoadmap;
    private CardView cardAssessments;
    private CardView cardSimulation;
    private CardView cardCollaboration;
    private CardView cardAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect this Java class to activity_main.xml
        setContentView(R.layout.activity_main);

        // Initialise Dashboard views
        initialiseViews();

        // Set Dashboard interactions
        setupClickListeners();
    }

    /**
     * Finds the views from activity_main.xml
     * and assigns them to the Java variables.
     */
    private void initialiseViews() {

        cardBaseline = findViewById(R.id.cardBaseline);
        cardRoadmap = findViewById(R.id.cardRoadmap);
        cardAssessments = findViewById(R.id.cardAssessments);
        cardSimulation = findViewById(R.id.cardSimulation);
        cardCollaboration = findViewById(R.id.cardCollaboration);
        cardAchievements = findViewById(R.id.cardAchievements);
    }

    /**
     * Controls what happens when the user
     * interacts with Dashboard components.
     */
    private void setupClickListeners() {

        // Baseline Assessment
        cardBaseline.setOnClickListener(view -> {

            /*
             * The Baseline Assessment Activity will be
             * connected here once the assessment module
             * has been created by the responsible group member.
             */

            Toast.makeText(
                    MainActivity.this,
                    "Baseline Assessment selected",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // Roadmap - currently locked
        cardRoadmap.setOnClickListener(view -> {

            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock your Roadmap.",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // Assessments - currently locked
        cardAssessments.setOnClickListener(view -> {

            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Assessments.",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // Simulation - currently locked
        cardSimulation.setOnClickListener(view -> {

            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Simulation.",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // Collaboration - currently locked
        cardCollaboration.setOnClickListener(view -> {

            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Collaboration.",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // Achievements - currently locked
        cardAchievements.setOnClickListener(view -> {

            Toast.makeText(
                    MainActivity.this,
                    "Complete the Baseline Assessment to unlock Achievements.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}