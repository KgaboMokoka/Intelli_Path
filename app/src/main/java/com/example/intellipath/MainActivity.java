package com.example.intellipath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private TextView tvRoadmapStatus;
    private TextView tvAssessmentsStatus;
    private TextView tvSimulationStatus;
    private TextView tvCollaborationStatus;
    private TextView tvAchievementsStatus;

    private Button btnLogout;


    // ============================================================
    // DASHBOARD PREFERENCES
    // ============================================================

    private SharedPreferences dashboardPreferences;

    private static final String PREFS_NAME =
            "IntelliPathDashboard";

    private static final String KEY_BASELINE_COMPLETED =
            "baseline_completed";

    private static final String KEY_READINESS_SCORE =
            "readiness_score";


    // ============================================================
    // BASELINE ASSESSMENT ID
    // ============================================================

    // ID of the Baseline Assessment row in the assessments table
    private static final String BASELINE_ASSESSMENT_ID =
            "c146a692-332e-4593-b1b1-3f4e198a6794";


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

        // Set Dashboard interactions
        setupClickListeners();

        /*
         * Profile and dashboard state are loaded in onResume(),
         * which always runs right after onCreate().
         */
    }


    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    private void initialiseViews() {

        // --------------------------------------------------------
        // Dashboard text
        // --------------------------------------------------------

        tvUserName =
                findViewById(R.id.tvGreeting);

        tvReadinessScore =
                findViewById(R.id.tvReadinessScore);

        tvReadinessStatus =
                findViewById(R.id.tvBaselineMessage);

        tvRoadmapStatus =
                findViewById(R.id.tvRoadmapStatus);

        tvAssessmentsStatus =
                findViewById(R.id.tvAssessmentsStatus);

        tvSimulationStatus =
                findViewById(R.id.tvSimulationStatus);

        tvCollaborationStatus =
                findViewById(R.id.tvCollaborationStatus);

        tvAchievementsStatus =
                findViewById(R.id.tvAchievementsStatus);

        btnLogout =
                findViewById(R.id.btnLogout);


        // --------------------------------------------------------
        // Dashboard cards
        // --------------------------------------------------------

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

                        /*
                         * Do not prevent Dashboard from opening
                         * if the profile cannot be retrieved.
                         */

                        tvUserName.setText(
                                "Hello, Student!"
                        );
                    }
                }
        );
    }


    // ============================================================
    // LOAD DASHBOARD STATE FROM SUPABASE
    // ============================================================

    private void loadDashboardState() {

        /*
         * Keep Baseline and Collaboration available at all times.
         * The other four cards remain locked until the database
         * confirms that the Baseline Assessment is completed.
         */

        unlockCard(cardBaseline);
        unlockCard(cardCollaboration);

        updateCardStatus(
                tvCollaborationStatus,
                true
        );

        // Neutral state while the database is being checked
        tvReadinessScore.setText("Loading...");

        tvReadinessStatus.setVisibility(
                View.GONE
        );


        // Temporarily show locked state for the cards
        // that depend on the baseline assessment.
        lockCard(cardRoadmap);
        lockCard(cardAssessments);
        lockCard(cardSimulation);
        lockCard(cardAchievements);

        updateCardStatus(
                tvRoadmapStatus,
                false
        );

        updateCardStatus(
                tvAssessmentsStatus,
                false
        );

        updateCardStatus(
                tvSimulationStatus,
                false
        );

        updateCardStatus(
                tvAchievementsStatus,
                false
        );


        // --------------------------------------------------------
        // Fetch latest baseline result from Supabase
        // --------------------------------------------------------

        AssessmentRepository.fetchLatestBaselineResult(
                BASELINE_ASSESSMENT_ID,
                new AssessmentRepository.Callback<StudentAssessmentReadRow>() {

                    @Override
                    public void onSuccess(
                            StudentAssessmentReadRow result
                    ) {

                        SharedPreferences.Editor editor =
                                dashboardPreferences.edit();


                        // ====================================================
                        // BASELINE NOT COMPLETED
                        // ====================================================

                        if (result == null) {

                            editor.putBoolean(
                                    KEY_BASELINE_COMPLETED,
                                    false
                            );

                            editor.remove(
                                    KEY_READINESS_SCORE
                            );

                            editor.apply();

                            showBaselineNotCompleted();

                            return;
                        }


                        // ====================================================
                        // BASELINE COMPLETED
                        // ====================================================

                        String score = "";

                        if (result.getPercentage() != null) {

                            score =
                                    String.valueOf(
                                            Math.round(
                                                    result.getPercentage()
                                            )
                                    );
                        }


                        editor.putBoolean(
                                KEY_BASELINE_COMPLETED,
                                true
                        );

                        editor.putString(
                                KEY_READINESS_SCORE,
                                score
                        );

                        editor.apply();


                        showBaselineCompleted(
                                score
                        );
                    }


                    @Override
                    public void onError(String message) {

                        /*
                         * If the database cannot be reached,
                         * keep the dependent cards locked.
                         *
                         * Baseline and Collaboration remain
                         * available.
                         */

                        tvReadinessScore.setText(
                                "Unavailable"
                        );

                        tvReadinessStatus.setVisibility(
                                View.GONE
                        );

                        lockDashboardCards();
                    }
                }
        );
    }


    // ============================================================
    // BASELINE NOT COMPLETED
    // ============================================================

    private void showBaselineNotCompleted() {

        tvReadinessScore.setText(
                "Not Calculated Yet"
        );


        /*
         * Show this message ONLY when the user has not
         * completed the Baseline Assessment.
         */

        tvReadinessStatus.setVisibility(
                View.VISIBLE
        );

        tvReadinessStatus.setText(
                "Complete your Baseline Assessment to unlock "
                        + "your personalised roadmap and progress tracking."
        );


        /*
         * Baseline and Collaboration remain unlocked.
         * Roadmap, Assessments, Simulation and Achievements
         * remain locked.
         */

        lockDashboardCards();
    }


    // ============================================================
    // BASELINE COMPLETED
    // ============================================================

    private void showBaselineCompleted(
            String score
    ) {

        if (score == null ||
                score.trim().isEmpty()) {

            tvReadinessScore.setText(
                    "Calculated"
            );

        } else {

            tvReadinessScore.setText(
                    score + "%"
            );
        }


        /*
         * Hide the baseline instruction completely once
         * the user has completed the assessment.
         */

        tvReadinessStatus.setVisibility(
                View.GONE
        );


        /*
         * Unlock all dashboard cards.
         */

        unlockDashboardCards();
    }


    // ============================================================
    // LOCK DASHBOARD CARDS
    // ============================================================

    private void lockDashboardCards() {

        /*
         * BASELINE:
         * Always unlocked.
         */

        unlockCard(
                cardBaseline
        );


        /*
         * COLLABORATION:
         * Always unlocked.
         */

        unlockCard(
                cardCollaboration
        );


        /*
         * Collaboration should always display Open.
         */

        updateCardStatus(
                tvCollaborationStatus,
                true
        );


        /*
         * ROADMAP:
         * Locked until baseline is completed.
         */

        lockCard(
                cardRoadmap
        );

        updateCardStatus(
                tvRoadmapStatus,
                false
        );


        /*
         * ASSESSMENTS:
         * Locked until baseline is completed.
         */

        lockCard(
                cardAssessments
        );

        updateCardStatus(
                tvAssessmentsStatus,
                false
        );


        /*
         * SIMULATION:
         * Locked until baseline is completed.
         */

        lockCard(
                cardSimulation
        );

        updateCardStatus(
                tvSimulationStatus,
                false
        );


        /*
         * ACHIEVEMENTS:
         * Locked until baseline is completed.
         */

        lockCard(
                cardAchievements
        );

        updateCardStatus(
                tvAchievementsStatus,
                false
        );
    }


    // ============================================================
    // UNLOCK DASHBOARD CARDS
    // ============================================================

    private void unlockDashboardCards() {

        /*
         * All six cards are available once the
         * Baseline Assessment is completed.
         */

        unlockCard(
                cardBaseline
        );

        unlockCard(
                cardRoadmap
        );

        unlockCard(
                cardAssessments
        );

        unlockCard(
                cardSimulation
        );

        unlockCard(
                cardCollaboration
        );

        unlockCard(
                cardAchievements
        );


        /*
         * Change the status of the cards from
         * "Locked" to "Open →".
         */

        updateCardStatus(
                tvRoadmapStatus,
                true
        );

        updateCardStatus(
                tvAssessmentsStatus,
                true
        );

        updateCardStatus(
                tvSimulationStatus,
                true
        );

        updateCardStatus(
                tvCollaborationStatus,
                true
        );

        updateCardStatus(
                tvAchievementsStatus,
                true
        );
    }


    // ============================================================
    // UPDATE CARD STATUS
    // ============================================================

    private void updateCardStatus(
            TextView statusView,
            boolean unlocked
    ) {

        if (statusView == null) {
            return;
        }


        if (unlocked) {

            statusView.setText(
                    "Open →"
            );

            statusView.setTextColor(
                    android.graphics.Color.parseColor(
                            "#6744B7"
                    )
            );

        } else {

            statusView.setText(
                    "🔒 Locked"
            );

            statusView.setTextColor(
                    android.graphics.Color.parseColor(
                            "#777A83"
                    )
            );
        }
    }


    // ============================================================
    // LOCK CARD
    // ============================================================

    private void lockCard(
            CardView card
    ) {

        if (card == null) {
            return;
        }

        card.setEnabled(
                false
        );

        card.setAlpha(
                0.55f
        );
    }


    // ============================================================
    // UNLOCK CARD
    // ============================================================

    private void unlockCard(
            CardView card
    ) {

        if (card == null) {
            return;
        }

        card.setEnabled(
                true
        );

        card.setAlpha(
                1.0f
        );
    }


    // ============================================================
    // CLICK LISTENERS
    // ============================================================

    private void setupClickListeners() {


        // ========================================================
        // LOGOUT
        // ========================================================

        btnLogout.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    LoginPage.class
                            );

                    /*
                     * Clear the current Dashboard from
                     * the back stack so the user cannot
                     * press Back and return to it.
                     */

                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(
                            intent
                    );

                    finish();
                }
        );


        // ========================================================
        // BASELINE ASSESSMENT
        // ========================================================

        cardBaseline.setOnClickListener(
                view -> {

                    if (!isBaselineCompleted()) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        BaselineIntroductionActivity.class
                                );

                        startActivity(
                                intent
                        );

                    } else {

                        Toast.makeText(
                                MainActivity.this,
                                "Baseline Assessment already completed.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );


        // ========================================================
        // ROADMAP
        // ========================================================

        cardRoadmap.setOnClickListener(
                view -> {

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

                        startActivity(
                                intent
                        );
                    }
                }
        );


        // ========================================================
        // ASSESSMENTS
        // ========================================================

        cardAssessments.setOnClickListener(
                view -> {

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
                }
        );


        // ========================================================
        // SIMULATION
        // ========================================================

        cardSimulation.setOnClickListener(
                view -> {

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

                        startActivity(
                                intent
                        );
                    }
                }
        );


        // ========================================================
        // COLLABORATION
        // ========================================================

        cardCollaboration.setOnClickListener(
                view -> {

                    /*
                     * Collaboration is available to everyone,
                     * even if the Baseline Assessment has not
                     * been completed.
                     */

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    CollaborationActivity.class
                            );

                    startActivity(
                            intent
                    );
                }
        );


        // ========================================================
        // ACHIEVEMENTS
        // ========================================================

        cardAchievements.setOnClickListener(
                view -> {

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
                }
        );
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

    private void showLockedMessage(
            String message
    ) {

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