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
import com.example.intellipath.data.AssessmentRepository;
import com.example.intellipath.data.StudentAssessmentReadRow;

public class MainActivity extends AppCompatActivity {

    private static final String BASELINE_ASSESSMENT_ID =
            "c146a692-332e-4593-b1b1-3f4e198a6794";

    private boolean baselineCompletedCache = false;
    private int baselinePercentageCache = 0;

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

        // Initialise local preferences
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

        tvReadinessScore.setText("Loading...");
        tvReadinessStatus.setText(
                "Checking your assessment status..."
        );

        lockDashboardCards();

        AssessmentRepository.fetchLatestBaselineResult(
                BASELINE_ASSESSMENT_ID,
                new AssessmentRepository.Callback<StudentAssessmentReadRow>() {

                    @Override
                    public void onSuccess(
                            StudentAssessmentReadRow result
                    ) {

                        if (result == null) {

                            baselineCompletedCache = false;

                            tvReadinessScore.setText(
                                    "Not Calculated Yet"
                            );

                            tvReadinessStatus.setText(
                                    "Complete your Baseline Assessment to unlock "
                                            + "your personalised roadmap and progress tracking."
                            );

                            lockDashboardCards();

                        } else {

                            baselineCompletedCache = true;

                            Double percentage =
                                    result.getPercentage();

                            if (percentage != null) {

                                baselinePercentageCache =
                                        (int) Math.round(percentage);

                                tvReadinessScore.setText(
                                        Math.round(percentage) + "%"
                                );

                            } else {

                                tvReadinessScore.setText(
                                        "Calculated"
                                );
                            }

                            tvReadinessStatus.setText(
                                    "Your personalised readiness profile is ready."
                            );

                            unlockDashboardCards();
                        }
                    }

                    @Override
                    public void onError(String message) {

                        // Fail safe: treat as not-yet-completed rather than
                        // blocking the dashboard entirely.

                        baselineCompletedCache = false;

                        tvReadinessScore.setText(
                                "Not Calculated Yet"
                        );

                        tvReadinessStatus.setText(
                                "Complete your Baseline Assessment to unlock "
                                        + "your personalised roadmap and progress tracking."
                        );

                        lockDashboardCards();
                    }
                }
        );
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
                    isBaselineCompleted();

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

                Intent intent = new Intent(
                        MainActivity.this,
                        GeneratedRoadmapActivity.class
                );

                intent.putExtra("percentage", baselinePercentageCache);

                startActivity(intent);
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

                startActivity(intent);
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

        return baselineCompletedCache;
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