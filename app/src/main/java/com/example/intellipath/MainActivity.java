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

    private static final String PREFS_NAME = "IntelliPathDashboard";
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

        // Connect this Java class to activity_main.xml
        setContentView(R.layout.activity_main);

        // Initialise local preferences
        dashboardPreferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );

        // Find Dashboard views
        initialiseViews();

        // Load current student's information
        loadStudentProfile();

        // Load the current Dashboard state
        loadDashboardState();

        // Set Dashboard interactions
        setupClickListeners();
    }


    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    /**
     * Finds the views from activity_main.xml
     * and assigns them to the Java variables.
     */
    private void initialiseViews() {

        // Dashboard cards
        cardBaseline = findViewById(R.id.cardBaseline);
        cardRoadmap = findViewById(R.id.cardRoadmap);
        cardAssessments = findViewById(R.id.cardAssessments);
        cardSimulation = findViewById(R.id.cardSimulation);
        cardCollaboration = findViewById(R.id.cardCollaboration);
        cardAchievements = findViewById(R.id.cardAchievements);
    }


    // ============================================================
    // LOAD STUDENT PROFILE
    // ============================================================

    /**
     * Retrieves the currently logged-in student's profile
     * from Supabase.
     *
     * The first name is then displayed on the Dashboard.
     */
    private void loadStudentProfile() {

        SupabaseAuthRepository.getCurrentStudentProfile(
                new SupabaseAuthRepository.StudentProfileCallback() {

                    @Override
                    public void onSuccess(StudentRow student) {

                        String firstName = student.getFirst_name();

                        if (firstName == null ||
                                firstName.trim().isEmpty()) {

                            tvUserName.setText("Student");

                        } else {

                            tvUserName.setText(firstName);
                        }
                    }

                    @Override
                    public void onError(String message) {

                        /*
                         * Do not prevent the Dashboard from opening
                         * if the profile cannot be retrieved.
                         */
                        tvUserName.setText("Student");
                    }
                }
        );
    }


    // ============================================================
    // LOAD DASHBOARD STATE
    // ============================================================

    /**
     * Loads the student's current Dashboard state.
     *
     * At the moment the baseline completion state is stored
     * locally. This will later be replaced/combined with the
     * Supabase assessment_results data once the database
     * relationship is connected.
     */
    private void loadDashboardState() {

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

        // --------------------------------------------------------
        // BASELINE NOT COMPLETED
        // --------------------------------------------------------

        if (!baselineCompleted) {

            tvReadinessScore.setText(
                    "Not Calculated Yet"
            );

            tvReadinessStatus.setText(
                    "Complete your Baseline Assessment to unlock "
                            + "your personalised roadmap and progress tracking."
            );

            lockDashboardCards();

        }

        // --------------------------------------------------------
        // BASELINE COMPLETED
        // --------------------------------------------------------

        else {

            if (savedReadinessScore == null ||
                    savedReadinessScore.trim().isEmpty()) {

                /*
                 * The score will eventually come from
                 * assessment_results.readiness_score.
                 */
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

            unlockDashboardCards();
        }
    }


    // ============================================================
    // LOCK DASHBOARD CARDS
    // ============================================================

    /**
     * Keeps the Baseline Assessment available while
     * locking the remaining Dashboard cards.
     */
    private void lockDashboardCards() {

        // Baseline remains available
        cardBaseline.setEnabled(true);
        cardBaseline.setAlpha(1.0f);

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

    /**
     * Makes all Dashboard journey cards available
     * after the Baseline Assessment is completed.
     */
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

        /*
         * Slightly faded appearance so the user can visually
         * identify that the card is locked.
         */
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

    /**
     * Controls what happens when the user interacts
     * with Dashboard components.
     */
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

                /*
                 * Open the Baseline Assessment.
                 *
                 * If your BaselineTestActivity already exists,
                 * this will open it directly.
                 */
                Intent intent = new Intent(
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

                /*
                 * Roadmap Activity will be connected here
                 * once the responsible group member's Activity
                 * is available.
                 */
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
                 * Assessments Activity will be connected here
                 * once available.
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

                /*
                 * Simulation Activity will be connected here
                 * once available.
                 */
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

                /*
                 * Collaboration Activity will be connected here
                 * once available.
                 */
                Toast.makeText(
                        MainActivity.this,
                        "Collaboration selected.",
                        Toast.LENGTH_SHORT
                ).show();
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
                 * Achievements Activity will be connected here
                 * once available.
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

    /**
     * Refresh the Dashboard whenever the user returns to it.
     *
     */
    @Override
    protected void onResume() {

        super.onResume();

        /*
         * Avoid trying to access views before onCreate()
         * has initialised them.
         */
        if (dashboardPreferences != null) {

            loadDashboardState();
            loadStudentProfile();
        }
    }
}