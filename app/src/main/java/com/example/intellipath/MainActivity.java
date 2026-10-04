package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.intellipath.data.AssessmentRepository;
import com.example.intellipath.data.StudentAssessmentReadRow;
import com.example.intellipath.data.StudentRow;
import com.example.intellipath.data.SupabaseAuthRepository;
import com.example.intellipath.sections.systemarchitecture.LabSimulationActivity;

public class MainActivity extends AppCompatActivity {

    // ID of the baseline row in the assessments table
    private static final String BASELINE_ASSESSMENT_ID =
            "c146a692-332e-4593-b1b1-3f4e198a6794";

    // ============================================================
    // DASHBOARD STATE (Supabase is the source of truth)
    // ============================================================

    private static final int PERCENTAGE_UNKNOWN = -1;

    private boolean baselineCompletedCache = false;
    private int baselinePercentageCache = PERCENTAGE_UNKNOWN;

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
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Find Dashboard views
        initialiseViews();

        // Set Dashboard interactions
        setupClickListeners();

        // Profile and dashboard state are loaded in onResume(),
        // which always runs right after onCreate().
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

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

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

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

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
    // LOAD DASHBOARD STATE (FROM SUPABASE)
    // ============================================================

    private void loadDashboardState() {

        // Reset until the database answers
        baselineCompletedCache = false;
        baselinePercentageCache = PERCENTAGE_UNKNOWN;

        tvReadinessScore.setText("Loading...");
        tvReadinessStatus.setText(
                "Checking your assessment status..."
        );

        // Everything stays locked (including Baseline) while loading,
        // so a student can't start the test before we know their status.
        lockDashboardCards();
        lockCard(cardBaseline);

        AssessmentRepository.fetchLatestBaselineResult(
                BASELINE_ASSESSMENT_ID,
                new AssessmentRepository.Callback<StudentAssessmentReadRow>() {

                    @Override
                    public void onSuccess(
                            StudentAssessmentReadRow result
                    ) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        // No completed baseline for this student
                        if (result == null) {

                            baselineCompletedCache = false;
                            baselinePercentageCache = PERCENTAGE_UNKNOWN;

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

                        baselineCompletedCache = true;

                        Double percentage =
                                result.getPercentage();

                        if (percentage != null) {

                            baselinePercentageCache =
                                    (int) Math.round(percentage);

                            tvReadinessScore.setText(
                                    baselinePercentageCache + "%"
                            );

                        } else {

                            baselinePercentageCache = PERCENTAGE_UNKNOWN;

                            tvReadinessScore.setText(
                                    "Calculated"
                            );
                        }

                        tvReadinessStatus.setText(
                                "Your personalised readiness profile is ready."
                        );

                        unlockDashboardCards();
                    }

                    @Override
                    public void onError(String message) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        // We don't know the student's status, so don't let
                        // them start the baseline again by accident.

                        baselineCompletedCache = false;
                        baselinePercentageCache = PERCENTAGE_UNKNOWN;

                        tvReadinessScore.setText("Unavailable");

                        tvReadinessStatus.setText(
                                "Could not load your results: " + message
                        );

                        lockDashboardCards();
                        lockCard(cardBaseline);
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

            if (!isBaselineCompleted()) {

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

                intent.putExtra(
                        GeneratedRoadmapActivity.EXTRA_PERCENTAGE,
                        baselinePercentageCache
                );

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
         * Runs after onCreate() and every time the user comes back
         * to the Dashboard (for example after finishing the baseline),
         * so the score is always re-read from Supabase.
         */

        loadStudentProfile();
        loadDashboardState();
    }
}