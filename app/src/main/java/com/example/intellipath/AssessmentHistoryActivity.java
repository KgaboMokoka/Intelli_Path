
package com.example.intellipath;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.intellipath.data.AssessmentHistoryRepository;
import com.example.intellipath.data.AssessmentHistoryRepository.AssessmentHistoryItem;
import com.example.intellipath.data.AssessmentHistoryRepository.CompletedLabHistoryItem;

import java.util.List;
import java.util.Locale;

public class AssessmentHistoryActivity extends AppCompatActivity {

    // ============================================================
    // COLOURS
    // ============================================================

    private static final int NAVY = Color.parseColor("#080B2A");
    private static final int PURPLE = Color.parseColor("#6744B7");
    private static final int BACKGROUND = Color.parseColor("#F8F8FC");
    private static final int DARK_TEXT = Color.parseColor("#10142F");
    private static final int SECONDARY_TEXT = Color.parseColor("#626477");
    private static final int LIGHT_PURPLE = Color.parseColor("#F0EFF8");
    private static final int WHITE = Color.WHITE;

    // ============================================================
    // VIEWS
    // ============================================================

    private LinearLayout assessmentsContainer;
    private LinearLayout labsContainer;

    private TextView tvAssessmentCount;
    private TextView tvLabCount;
    private TextView tvLoadingMessage;
    private TextView tvEmptyAssessments;
    private TextView tvEmptyLabs;

    private ProgressBar progressBar;
    private View btnRetry;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assessment_history);

        initialiseViews();
        setupClickListeners();
        setupBottomNavigation();
        loadAssessmentHistory();
    }

    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    private void initialiseViews() {

        assessmentsContainer = findViewById(R.id.assessmentsContainer);
        labsContainer = findViewById(R.id.labsContainer);

        tvAssessmentCount = findViewById(R.id.tvAssessmentCount);
        tvLabCount = findViewById(R.id.tvLabCount);

        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        tvEmptyAssessments = findViewById(R.id.tvEmptyAssessments);
        tvEmptyLabs = findViewById(R.id.tvEmptyLabs);

        progressBar = findViewById(R.id.historyProgressBar);
        btnRetry = findViewById(R.id.btnRetry);
    }

    // ============================================================
    // CLICK LISTENERS
    // ============================================================

    private void setupClickListeners() {

        findViewById(R.id.btnBack).setOnClickListener(
                view -> finish()
        );

        btnRetry.setOnClickListener(
                view -> loadAssessmentHistory()
        );
    }

    // ============================================================
    // BOTTOM NAVIGATION
    // ============================================================

    private void setupBottomNavigation() {

        // HOME
        findViewById(R.id.navDashboard).setOnClickListener(view -> {

            Intent intent = new Intent(
                    AssessmentHistoryActivity.this,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        // ASSESSMENTS
        // The user is already viewing Assessment History.
        findViewById(R.id.navAssessments).setOnClickListener(view -> {
            // No navigation required.
        });

        // PROGRESS
        findViewById(R.id.navProgress).setOnClickListener(view -> {
            Toast.makeText(
                    AssessmentHistoryActivity.this,
                    "Progress selected.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // PROFILE
        findViewById(R.id.navProfile).setOnClickListener(view -> {
            Toast.makeText(
                    AssessmentHistoryActivity.this,
                    "Profile selected.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // ============================================================
    // LOAD HISTORY FROM SUPABASE
    // ============================================================

    private void loadAssessmentHistory() {

        showLoading(true);

        btnRetry.setVisibility(View.GONE);

        assessmentsContainer.removeAllViews();
        labsContainer.removeAllViews();

        tvEmptyAssessments.setVisibility(View.GONE);
        tvEmptyLabs.setVisibility(View.GONE);

        AssessmentHistoryRepository.loadHistory(
                new AssessmentHistoryRepository.Callback() {

                    @Override
                    public void onSuccess(
                            List<AssessmentHistoryItem> assessments,
                            List<CompletedLabHistoryItem> labs
                    ) {

                        showLoading(false);

                        tvAssessmentCount.setText(
                                String.valueOf(assessments.size())
                        );

                        tvLabCount.setText(
                                String.valueOf(labs.size())
                        );

                        // COMPLETED ASSESSMENTS

                        if (assessments.isEmpty()) {

                            tvEmptyAssessments.setVisibility(View.VISIBLE);

                        } else {

                            tvEmptyAssessments.setVisibility(View.GONE);

                            for (AssessmentHistoryItem assessment : assessments) {
                                addAssessmentCard(assessment);
                            }
                        }

                        // COMPLETED ROADMAP LABS

                        if (labs.isEmpty()) {

                            tvEmptyLabs.setVisibility(View.VISIBLE);

                        } else {

                            tvEmptyLabs.setVisibility(View.GONE);

                            for (CompletedLabHistoryItem lab : labs) {
                                addLabCard(lab);
                            }
                        }
                    }

                    @Override
                    public void onError(String message) {

                        showLoading(false);

                        tvLoadingMessage.setText(
                                "We couldn't load your history. "
                                        + "Please check your connection and try again."
                        );

                        tvLoadingMessage.setVisibility(View.VISIBLE);
                        btnRetry.setVisibility(View.VISIBLE);

                        Toast.makeText(
                                AssessmentHistoryActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // ============================================================
    // LOADING STATE
    // ============================================================

    private void showLoading(boolean loading) {

        progressBar.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );

        tvLoadingMessage.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );

        if (loading) {
            tvLoadingMessage.setText(
                    "Loading your assessment and lab history..."
            );
        }
    }

    // ============================================================
    // ASSESSMENT CARD
    // ============================================================

    private void addAssessmentCard(AssessmentHistoryItem assessment) {

        CardView card = createCard();
        LinearLayout content = createVerticalLayout(14);
        card.addView(content);

        // TITLE AND COMPLETED BADGE

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        content.addView(titleRow);

        TextView title = createText(
                assessment.getTitle(),
                15,
                DARK_TEXT,
                true
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        titleRow.addView(title, titleParams);
        titleRow.addView(createBadge("Completed"));

        // ASSESSMENT TYPE

        String type = safeText(
                assessment.getType(),
                "Assessment"
        );

        TextView typeView = createText(
                type,
                11,
                SECONDARY_TEXT,
                false
        );

        LinearLayout.LayoutParams typeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        typeParams.topMargin = dp(5);
        content.addView(typeView, typeParams);

        // SCORE, PERCENTAGE AND READINESS

        LinearLayout scoreRow = new LinearLayout(this);
        scoreRow.setOrientation(LinearLayout.HORIZONTAL);
        scoreRow.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams scoreRowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        scoreRowParams.topMargin = dp(14);
        content.addView(scoreRow, scoreRowParams);

        addMetric(
                scoreRow,
                "Score",
                assessment.getScore() == null
                        ? "Not recorded"
                        : String.valueOf(assessment.getScore())
        );

        addMetric(
                scoreRow,
                "Percentage",
                assessment.getPercentage() == null
                        ? "Not recorded"
                        : String.format(
                        Locale.getDefault(),
                        "%.1f%%",
                        assessment.getPercentage()
                )
        );

        if (assessment.getReadinessScore() != null) {
            addMetric(
                    scoreRow,
                    "Readiness",
                    String.format(
                            Locale.getDefault(),
                            "%.1f",
                            assessment.getReadinessScore()
                    )
            );
        }

        // OPTIONAL METADATA

        if (assessment.getTotalQuestions() != null) {
            addDetail(
                    content,
                    "Questions",
                    String.valueOf(assessment.getTotalQuestions())
            );
        }

        if (assessment.getDurationMinutes() != null) {
            addDetail(
                    content,
                    "Duration",
                    assessment.getDurationMinutes() + " minutes"
            );
        }

        // COMPLETION DATE

        addDetail(
                content,
                "Completed",
                formatDate(assessment.getCompletedAt())
        );

        // STRENGTHS

        if (hasText(assessment.getStrengths())) {

            addDivider(content);
            addSectionLabel(content, "Strengths");

            TextView strengths = createText(
                    assessment.getStrengths(),
                    12,
                    SECONDARY_TEXT,
                    false
            );

            addTopMargin(content, strengths, 5);
        }

        // AREAS FOR IMPROVEMENT

        if (hasText(assessment.getAreasForImprovement())) {

            addSectionLabel(content, "Areas for Improvement");

            TextView improvements = createText(
                    assessment.getAreasForImprovement(),
                    12,
                    SECONDARY_TEXT,
                    false
            );

            addTopMargin(content, improvements, 5);
        }

        assessmentsContainer.addView(
                card,
                createCardLayoutParams()
        );
    }

    // ============================================================
    // COMPLETED ROADMAP LAB CARD
    // ============================================================

    private void addLabCard(CompletedLabHistoryItem lab) {

        CardView card = createCard();
        LinearLayout content = createVerticalLayout(14);
        card.addView(content);

        // TITLE ROW

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        content.addView(titleRow);

        TextView icon = createText(
                "✓",
                20,
                PURPLE,
                true
        );

        icon.setGravity(Gravity.CENTER);
        icon.setBackgroundColor(LIGHT_PURPLE);

        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(dp(38), dp(38));

        titleRow.addView(icon, iconParams);

        LinearLayout titleContent = createVerticalLayout(0);

        LinearLayout.LayoutParams titleContentParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        titleContentParams.leftMargin = dp(10);
        titleRow.addView(titleContent, titleContentParams);

        titleContent.addView(
                createText(
                        lab.getTitle(),
                        14,
                        DARK_TEXT,
                        true
                )
        );

        if (hasText(lab.getRoadmapTitle())) {

            TextView roadmapTitle = createText(
                    "Roadmap: " + lab.getRoadmapTitle(),
                    10,
                    SECONDARY_TEXT,
                    false
            );

            addTopMargin(titleContent, roadmapTitle, 4);
        }

        titleRow.addView(createBadge("Completed"));

        // DESCRIPTION

        if (hasText(lab.getDescription())) {

            TextView description = createText(
                    lab.getDescription(),
                    12,
                    SECONDARY_TEXT,
                    false
            );

            addTopMargin(content, description, 12);
        }

        // LANGUAGE AND DIFFICULTY

        if (hasText(lab.getLanguage())) {
            addDetail(content, "Language", lab.getLanguage());
        }

        if (hasText(lab.getDifficulty())) {
            addDetail(content, "Difficulty", lab.getDifficulty());
        }

        // COMPLETION DATE

        addDivider(content);

        addDetail(
                content,
                "Completed",
                formatDate(lab.getCompletedAt())
        );

        labsContainer.addView(
                card,
                createCardLayoutParams()
        );
    }

    // ============================================================
    // REUSABLE CARD HELPERS
    // ============================================================

    private CardView createCard() {

        CardView card = new CardView(this);

        card.setRadius(dp(14));
        card.setCardElevation(dp(3));
        card.setCardBackgroundColor(WHITE);
        card.setUseCompatPadding(true);

        return card;
    }

    private LinearLayout createVerticalLayout(int padding) {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        if (padding > 0) {
            layout.setPadding(
                    dp(padding),
                    dp(padding),
                    dp(padding),
                    dp(padding)
            );
        }

        return layout;
    }

    private LinearLayout.LayoutParams createCardLayoutParams() {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.bottomMargin = dp(12);
        return params;
    }

    private TextView createText(
            String value,
            int size,
            int colour,
            boolean bold
    ) {

        TextView textView = new TextView(this);

        textView.setText(value);
        textView.setTextSize(size);
        textView.setTextColor(colour);

        if (bold) {
            textView.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        textView.setLineSpacing(dp(2), 1.0f);

        return textView;
    }

    private TextView createBadge(String label) {

        TextView badge = createText(
                label,
                9,
                PURPLE,
                true
        );

        badge.setBackgroundColor(LIGHT_PURPLE);

        badge.setPadding(
                dp(8),
                dp(5),
                dp(8),
                dp(5)
        );

        return badge;
    }

    private void addMetric(
            LinearLayout parent,
            String label,
            String value
    ) {

        LinearLayout metric = createVerticalLayout(0);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        params.rightMargin = dp(6);
        parent.addView(metric, params);

        metric.addView(
                createText(label, 10, SECONDARY_TEXT, false)
        );

        TextView valueView = createText(
                value,
                14,
                PURPLE,
                true
        );

        addTopMargin(metric, valueView, 4);
    }

    private void addDetail(
            LinearLayout parent,
            String label,
            String value
    ) {

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.topMargin = dp(9);
        parent.addView(row, rowParams);

        TextView labelView = createText(
                label,
                11,
                SECONDARY_TEXT,
                false
        );

        row.addView(
                labelView,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView valueView = createText(
                value,
                11,
                DARK_TEXT,
                true
        );

        valueView.setGravity(Gravity.END);
        row.addView(valueView);
    }

    private void addSectionLabel(
            LinearLayout parent,
            String label
    ) {

        TextView heading = createText(
                label,
                12,
                PURPLE,
                true
        );

        addTopMargin(parent, heading, 12);
    }

    private void addDivider(LinearLayout parent) {

        View divider = new View(this);

        divider.setBackgroundColor(
                Color.parseColor("#E8E5F2")
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        params.topMargin = dp(14);
        parent.addView(divider, params);
    }

    private void addTopMargin(
            LinearLayout parent,
            View view,
            int margin
    ) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.topMargin = dp(margin);
        parent.addView(view, params);
    }

    // ============================================================
    // TEXT AND DATE HELPERS
    // ============================================================

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String safeText(String value, String fallback) {
        return hasText(value) ? value : fallback;
    }

    private String formatDate(String value) {

        if (!hasText(value)) {
            return "Date not recorded";
        }

        String formatted = value.trim().replace('T', ' ');

        if (formatted.length() > 19) {
            formatted = formatted.substring(0, 19);
        }

        return formatted;
    }

    private int dp(int value) {
        return Math.round(
                value * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
