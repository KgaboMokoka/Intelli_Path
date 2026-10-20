
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

import com.example.intellipath.data.AchievementRepository;
import com.example.intellipath.data.AchievementRepository.AssessmentFeedback;
import com.example.intellipath.data.AchievementRepository.EarnedBadge;

import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    // ============================================================
    // COLOURS
    // ============================================================

    private static final int NAVY =
            Color.parseColor("#080B2A");

    private static final int PURPLE =
            Color.parseColor("#6744B7");

    private static final int DARK_TEXT =
            Color.parseColor("#10142F");

    private static final int SECONDARY_TEXT =
            Color.parseColor("#626477");

    private static final int LIGHT_PURPLE =
            Color.parseColor("#F0EFF8");

    private static final int WHITE = Color.WHITE;

    // ============================================================
    // VIEWS
    // ============================================================

    private LinearLayout badgesContainer;
    private LinearLayout strengthsContainer;
    private LinearLayout improvementsContainer;

    private TextView tvBadgeCount;
    private TextView tvFeedbackCount;
    private TextView tvLoadingMessage;

    private TextView tvEmptyBadges;
    private TextView tvEmptyStrengths;
    private TextView tvEmptyImprovements;

    private ProgressBar progressBar;
    private View btnRetry;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_achievements);

        initialiseViews();
        setupClickListeners();
        setupBottomNavigation();
        loadAchievements();
    }

    // ============================================================
    // INITIALISE VIEWS
    // ============================================================

    private void initialiseViews() {

        badgesContainer = findViewById(R.id.badgesContainer);
        strengthsContainer = findViewById(R.id.strengthsContainer);
        improvementsContainer = findViewById(R.id.improvementsContainer);

        tvBadgeCount = findViewById(R.id.tvBadgeCount);
        tvFeedbackCount = findViewById(R.id.tvFeedbackCount);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);

        tvEmptyBadges = findViewById(R.id.tvEmptyBadges);
        tvEmptyStrengths = findViewById(R.id.tvEmptyStrengths);
        tvEmptyImprovements = findViewById(R.id.tvEmptyImprovements);

        progressBar = findViewById(R.id.achievementsProgressBar);
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
                view -> loadAchievements()
        );
    }

    // ============================================================
    // BOTTOM NAVIGATION
    // ============================================================

    private void setupBottomNavigation() {

        // HOME
        findViewById(R.id.navDashboard).setOnClickListener(view -> {

            Intent intent = new Intent(
                    AchievementsActivity.this,
                    MainActivity.class
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
        });

        // ASSESSMENT HISTORY
        findViewById(R.id.navAssessments).setOnClickListener(view -> {

            Intent intent = new Intent(
                    AchievementsActivity.this,
                    AssessmentHistoryActivity.class
            );

            startActivity(intent);
        });

        // PROGRESS
        findViewById(R.id.navProgress).setOnClickListener(view -> {
            Toast.makeText(
                    AchievementsActivity.this,
                    "Please connect this button to your Progress activity.",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // PROFILE
        findViewById(R.id.navProfile).setOnClickListener(view -> {
            Toast.makeText(
                    AchievementsActivity.this,
                    "Please connect this button to your Profile activity.",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // ============================================================
    // LOAD ACHIEVEMENTS
    // ============================================================

    private void loadAchievements() {

        showLoading(true);
        btnRetry.setVisibility(View.GONE);

        badgesContainer.removeAllViews();
        strengthsContainer.removeAllViews();
        improvementsContainer.removeAllViews();

        tvEmptyBadges.setVisibility(View.GONE);
        tvEmptyStrengths.setVisibility(View.GONE);
        tvEmptyImprovements.setVisibility(View.GONE);

        AchievementRepository.loadAchievements(
                new AchievementRepository.Callback() {

                    @Override
                    public void onSuccess(
                            List<EarnedBadge> badges,
                            List<AssessmentFeedback> feedback
                    ) {

                        showLoading(false);

                        tvBadgeCount.setText(
                                String.valueOf(badges.size())
                        );

                        tvFeedbackCount.setText(
                                String.valueOf(feedback.size())
                        );

                        // EARNED BADGES

                        if (badges.isEmpty()) {
                            tvEmptyBadges.setVisibility(View.VISIBLE);
                        } else {
                            for (EarnedBadge badge : badges) {
                                addBadgeCard(badge);
                            }
                        }

                        // STRENGTHS AND IMPROVEMENTS

                        boolean hasStrengths = false;
                        boolean hasImprovements = false;

                        for (AssessmentFeedback item : feedback) {

                            if (hasText(item.getStrengths())) {
                                hasStrengths = true;
                                addFeedbackCard(
                                        strengthsContainer,
                                        item.getAssessmentTitle(),
                                        item.getStrengths(),
                                        item.getCompletedAt(),
                                        true
                                );
                            }

                            if (hasText(item.getAreasForImprovement())) {
                                hasImprovements = true;
                                addFeedbackCard(
                                        improvementsContainer,
                                        item.getAssessmentTitle(),
                                        item.getAreasForImprovement(),
                                        item.getCompletedAt(),
                                        false
                                );
                            }
                        }

                        if (!hasStrengths) {
                            tvEmptyStrengths.setVisibility(View.VISIBLE);
                        }

                        if (!hasImprovements) {
                            tvEmptyImprovements.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(String message) {

                        showLoading(false);

                        tvLoadingMessage.setText(
                                "We couldn't load your achievements. "
                                        + "Check your connection and try again."
                        );

                        tvLoadingMessage.setVisibility(View.VISIBLE);
                        btnRetry.setVisibility(View.VISIBLE);

                        Toast.makeText(
                                AchievementsActivity.this,
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
                    "Loading your badges and assessment feedback..."
            );
        }
    }

    // ============================================================
    // BADGE CARD
    // ============================================================

    private void addBadgeCard(EarnedBadge badge) {

        CardView card = createCard();

        LinearLayout content = createVerticalLayout(14);
        card.addView(content);

        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        content.addView(titleRow);

        // Badge icon placeholder, styled to match IntelliPath.
        // The repository preserves icon_url if you later add image loading.

        TextView icon = createText("★", 25, PURPLE, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackgroundColor(LIGHT_PURPLE);

        titleRow.addView(
                icon,
                new LinearLayout.LayoutParams(dp(48), dp(48))
        );

        LinearLayout details = createVerticalLayout(0);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        detailsParams.leftMargin = dp(12);
        titleRow.addView(details, detailsParams);

        details.addView(
                createText(
                        badge.getName(),
                        15,
                        DARK_TEXT,
                        true
                )
        );

        TextView earnedLabel = createText(
                "BADGE EARNED",
                9,
                PURPLE,
                true
        );

        addTopMargin(details, earnedLabel, 5);

        if (hasText(badge.getDescription())) {

            TextView description = createText(
                    badge.getDescription(),
                    12,
                    SECONDARY_TEXT,
                    false
            );

            addTopMargin(content, description, 12);
        }

        addDivider(content);

        addDetail(
                content,
                "Awarded",
                formatDate(badge.getAwardedAt())
        );

        badgesContainer.addView(
                card,
                createCardLayoutParams()
        );
    }

    // ============================================================
    // STRENGTH / IMPROVEMENT CARD
    // ============================================================

    private void addFeedbackCard(
            LinearLayout parent,
            String assessmentTitle,
            String feedbackText,
            String completedAt,
            boolean isStrength
    ) {

        CardView card = createCard();

        LinearLayout content = createVerticalLayout(14);
        card.addView(content);

        TextView assessment = createText(
                safeText(assessmentTitle, "Completed Assessment"),
                14,
                DARK_TEXT,
                true
        );

        content.addView(assessment);

        TextView category = createText(
                isStrength ? "STRENGTH IDENTIFIED" : "AREA TO DEVELOP",
                9,
                PURPLE,
                true
        );

        addTopMargin(content, category, 6);

        View divider = new View(this);
        divider.setBackgroundColor(
                Color.parseColor("#E8E5F2")
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerParams.topMargin = dp(10);
        content.addView(divider, dividerParams);

        TextView feedback = createText(
                feedbackText,
                12,
                SECONDARY_TEXT,
                false
        );

        addTopMargin(content, feedback, 10);

        addDetail(
                content,
                "Assessment completed",
                formatDate(completedAt)
        );

        parent.addView(
                card,
                createCardLayoutParams()
        );
    }

    // ============================================================
    // REUSABLE LAYOUT HELPERS
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
        textView.setLineSpacing(dp(2), 1.0f);

        if (bold) {
            textView.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return textView;
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
