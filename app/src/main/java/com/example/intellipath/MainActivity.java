package com.example.intellipath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.intellipath.data.StudentRow;
import com.example.intellipath.data.SupabaseAuthRepository;

public class MainActivity extends AppCompatActivity {

    // ============================================================
    // DASHBOARD CARDS
    // ============================================================

    private CardView cardBaseline;
    private CardView cardRoadmap;
    private CardView cardAssessments;
    private CardView cardSimulation;
    private CardView cardCollaboration;
    private CardView cardAchievements;


    // ============================================================
    // DASHBOARD TEXT
    // ============================================================

    private TextView tvUserName;
    private TextView tvReadinessScore;
    private TextView tvReadinessStatus;


    // ============================================================
    // LOCAL DASHBOARD STATE
    // ============================================================

    private SharedPreferences dashboardPreferences;

    private static final String PREFS_NAME =
            "IntelliPathDashboard";

    private static final String KEY_BASELINE_COMPLETED =
            "baseline_completed";

    private static final String KEY_READINESS_SCORE =
            "readiness_score";


    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        dashboardPreferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        // Find Dashboard views
        initialiseViews();

        // Load current student's information
        loadStudentProfile();

        // Load current Dashboard state
        loadDashboardState();

        // Set Dashboard interactions
        setupClickListeners();
    }


    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    private void initialiseViews() {

        // Dashboard text
        tvUserName =
                findViewById(R.id.tvGreeting);

        tvReadinessScore =
                findViewById(R.id.tvReadinessScore);

        tvReadinessStatus =
                findViewById(R.id.tvBaselineMessage);


        // Dashboard cards
        cardBaseline =
                findViewById(R.id.cardBaseline);

        cardRoadmap =
                findViewById(R.id.cardRoadmap);

        cardAssessments =
                findViewById(R.id.cardAssessments);

        cardSimulation =
                findViewById(R.id.cardSimulation);

        cardCollaboration =
                findViewById(R.id.cardCollaboration);

        cardAchievements =
                findViewById(R.id.cardAchievements);
    }


    // ============================================================
    // LOAD STUDENT PROFILE
    // ============================================================

    private void loadStudentProfile() {

        SupabaseAuthRepository.getCurrentStudentProfile(
                new SupabaseAuthRepository.StudentProfileCallback() {

                    @Override
                    public void onSuccess(StudentRow student) {

                        String firstName =
                                student.getFirst_name();

                        if (firstName == null ||
                                firstName.trim().isEmpty()) {

                            tvUserName.setText(
                                    "Hello, Student!"
                            );

                        } else {

                            tvUserName.setText(
                                    "Hello, " +
                                            firstName +
                                            "!"
                            );
                        }
                    }


                    @Override
                    public void onError(String message) {

                        // Do not prevent Dashboard from opening
                        // if the profile cannot be retrieved.

                        tvUserName.setText(
                                "Hello, Student!"
                        );
                    }
                }
        );
    }


    // ============================================================
    // LOAD DASHBOARD STATE
    // ============================================================

    private void loadDashboardState() {

        if (dashboardPreferences == null) {
            return;
        }


        boolean baselineCompleted =
                dashboardPreferences.getBoolean(
                        KEY_BASELINE_COMPLETED,
                        false
                );


        String savedReadinessScore =
                dashboardPreferences.getString(
                        KEY_READINESS_SCORE,
                        ""
                );


        // ========================================================
        // BASELINE NOT COMPLETED
        // ========================================================

        if (!baselineCompleted) {

            tvReadinessScore.setText(
                    "Not Calculated Yet"
            );

            tvReadinessStatus.setText(
                    "Complete your Baseline Assessment to unlock "
                            + "your personalised roadmap and progress tracking."
            );

            lockDashboardCards();

            return;
        }


        // ========================================================
        // BASELINE COMPLETED
        // ========================================================

        /*
         * Once the baseline is completed, the readiness score
         * should remain visible on the Dashboard.
         */

        if (savedReadinessScore == null ||
                savedReadinessScore.trim().isEmpty()) {

            tvReadinessScore.setText(
                    "Calculated"
            );

        } else {

            tvReadinessScore.setText(
                    savedReadinessScore + "%"
            );
        }


        tvReadinessStatus.setText(
                "Your personalised readiness profile is ready."
        );


        // Unlock all Dashboard cards
        unlockDashboardCards();
    }


    // ============================================================
    // LOCK DASHBOARD CARDS
    // ============================================================

    private void lockDashboardCards() {

        // Baseline remains available
        unlockCard(cardBaseline);


        // Remaining cards are locked
        lockCard(cardRoadmap);
        lockCard(cardAssessments);
        lockCard(cardSimulation);
        lockCard(cardCollaboration);
        lockCard(cardAchievements);
    }


    // ============================================================
    // UNLOCK DASHBOARD CARDS
    // ============================================================

    private void unlockDashboardCards() {

        unlockCard(cardBaseline);
        unlockCard(cardRoadmap);
        unlockCard(cardAssessments);
        unlockCard(cardSimulation);
        unlockCard(cardCollaboration);
        unlockCard(cardAchievements);
    }


    // ============================================================
    // LOCK CARD
    // ============================================================

    private void lockCard(CardView card) {

        if (card == null) {
            return;
        }

        card.setEnabled(false);
        card.setAlpha(0.55f);
    }


    // ============================================================
    // UNLOCK CARD
    // ============================================================

    private void unlockCard(CardView card) {

        if (card == null) {
            return;
        }

        card.setEnabled(true);
        card.setAlpha(1.0f);
    }


    // ============================================================
    // CLICK LISTENERS
    // ============================================================

    private void setupClickListeners() {


        // ========================================================
        // BASELINE ASSESSMENT
        // ========================================================

        cardBaseline.setOnClickListener(view -> {

            boolean baselineCompleted =
                    dashboardPreferences.getBoolean(
                            KEY_BASELINE_COMPLETED,
                            false
                    );


            if (!baselineCompleted) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                BaselineTestActivity.class
                        );

                startActivity(intent);

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Baseline Assessment already completed.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // ========================================================
        // ROADMAP
        // ========================================================

        cardRoadmap.setOnClickListener(view -> {

            if (!isBaselineCompleted()) {

                showLockedMessage(
                        "Complete the Baseline Assessment to unlock your Roadmap."
                );

            } else {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                RoadmapBeginner.class
                        );

                Toast.makeText(
                        MainActivity.this,
                        "Roadmap selected.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // ========================================================
        // ASSESSMENTS
        // ========================================================

        cardAssessments.setOnClickListener(view -> {

            if (!isBaselineCompleted()) {

                showLockedMessage(
                        "Complete the Baseline Assessment to unlock Assessments."
                );

            } else {

                /*
                 * Keep your existing Assessments navigation here
                 * when the Assessments Activity is connected.
                 */

                Toast.makeText(
                        MainActivity.this,
                        "Assessments selected.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // ========================================================
        // SIMULATION
        // ========================================================

        cardSimulation.setOnClickListener(view -> {

            if (!isBaselineCompleted()) {

                showLockedMessage(
                        "Complete the Baseline Assessment to unlock Simulation."
                );

            } else {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                LabSimulationActivity.class
                        );

                Toast.makeText(
                        MainActivity.this,
                        "Simulation selected.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // ========================================================
        // COLLABORATION
        // ========================================================

        cardCollaboration.setOnClickListener(view -> {

            if (!isBaselineCompleted()) {

                showLockedMessage(
                        "Complete the Baseline Assessment to unlock Collaboration."
                );

            } else {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                CollaborationActivity.class
                        );

                startActivity(intent);
            }
        });


        // ========================================================
        // ACHIEVEMENTS
        // ========================================================

        cardAchievements.setOnClickListener(view -> {

            if (!isBaselineCompleted()) {

                showLockedMessage(
                        "Complete the Baseline Assessment to unlock Achievements."
                );

            } else {

                /*
                 * Keep your existing Achievements navigation here
                 * when the Achievements Activity is connected.
                 */

                Toast.makeText(
                        MainActivity.this,
                        "Achievements selected.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }


    // ============================================================
    // CHECK BASELINE STATUS
    // ============================================================

    private boolean isBaselineCompleted() {

        if (dashboardPreferences == null) {
            return false;
        }

        return dashboardPreferences.getBoolean(
                KEY_BASELINE_COMPLETED,
                false
        );
    }


    // ============================================================
    // LOCKED CARD MESSAGE
    // ============================================================

    private void showLockedMessage(String message) {

        Toast.makeText(
                MainActivity.this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }


    // ============================================================
    // REFRESH DASHBOARD
    // ============================================================

    @Override
    protected void onResume() {

        super.onResume();

        /*
         * This is important.
         *
         * When the user opens a Dashboard card and later comes
         * back to MainActivity, the Dashboard state is loaded
         * again.
         *
         * This means the Industry Readiness Score remains visible
         * and the unlocked cards remain unlocked.
         */

        if (dashboardPreferences != null) {

            loadDashboardState();
            loadStudentProfile();
        }
    }
}