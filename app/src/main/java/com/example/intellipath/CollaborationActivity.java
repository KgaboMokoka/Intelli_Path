package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CollaborationActivity extends AppCompatActivity {

    // ============================================================
    // GROUP CARDS
    // ============================================================

    private CardView cardWorkplaceCrew;
    private CardView cardDeveloperSquad;
    private CardView cardCareerLaunchpad;
    private CardView cardProjectPartners;

    // ============================================================
    // NAVIGATION
    // ============================================================

    private LinearLayout navDashboard;
    private LinearLayout navAssessments;
    private LinearLayout navProgress;
    private LinearLayout navProfile;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collaboration);

        initialiseViews();
        setupClickListeners();
    }

    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    private void initialiseViews() {

        // Collaboration groups
        cardWorkplaceCrew = findViewById(R.id.cardWorkplaceCrew);
        cardDeveloperSquad = findViewById(R.id.cardDeveloperSquad);
        cardCareerLaunchpad = findViewById(R.id.cardCareerLaunchpad);
        cardProjectPartners = findViewById(R.id.cardProjectPartners);

        // Bottom navigation
        navDashboard = findViewById(R.id.navDashboard);
        navAssessments = findViewById(R.id.navAssessments);
        navProgress = findViewById(R.id.navProgress);
        navProfile = findViewById(R.id.navProfile);
    }

    // ============================================================
    // CLICK LISTENERS
    // ============================================================

    private void setupClickListeners() {

        // --------------------------------------------------------
        // WORKPLACE CREW
        // --------------------------------------------------------

        cardWorkplaceCrew.setOnClickListener(view -> {

            Intent intent = new Intent(
                    CollaborationActivity.this,
                    GroupInformationActivity.class
            );

            intent.putExtra("groupId", "workplace_crew");

            startActivity(intent);
        });

        // --------------------------------------------------------
        // DEVELOPER SQUAD
        // --------------------------------------------------------

        cardDeveloperSquad.setOnClickListener(view -> {

            Intent intent = new Intent(
                    CollaborationActivity.this,
                    GroupInformationActivity.class
            );

            intent.putExtra("groupId", "developer_squad");

            startActivity(intent);
        });

        // --------------------------------------------------------
        // CAREER LAUNCHPAD
        // --------------------------------------------------------

        cardCareerLaunchpad.setOnClickListener(view -> {

            Intent intent = new Intent(
                    CollaborationActivity.this,
                    GroupInformationActivity.class
            );

            intent.putExtra("groupId", "career_launchpad");

            startActivity(intent);
        });

        // --------------------------------------------------------
        // PROJECT PARTNERS
        // --------------------------------------------------------

        cardProjectPartners.setOnClickListener(view -> {

            Intent intent = new Intent(
                    CollaborationActivity.this,
                    GroupInformationActivity.class
            );

            intent.putExtra("groupId", "project_partners");

            startActivity(intent);
        });

        // --------------------------------------------------------
        // BOTTOM NAVIGATION
        // --------------------------------------------------------

        navDashboard.setOnClickListener(view -> {

            Intent intent = new Intent(
                    CollaborationActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });

        navAssessments.setOnClickListener(view -> {

            Toast.makeText(
                    CollaborationActivity.this,
                    "Assessments selected.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        navProgress.setOnClickListener(view -> {

            Toast.makeText(
                    CollaborationActivity.this,
                    "Progress selected.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        navProfile.setOnClickListener(view -> {

            Toast.makeText(
                    CollaborationActivity.this,
                    "Profile selected.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}
