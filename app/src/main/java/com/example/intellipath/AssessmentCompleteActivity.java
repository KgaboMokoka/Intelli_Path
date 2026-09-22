package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AssessmentCompleteActivity extends AppCompatActivity {

    private TextView tvScore;
    private TextView tvCorrect;
    private TextView tvIncorrect;
    private TextView tvPercentage;
    private TextView tvStrengths;
    private TextView tvAreasImprovement;
    private TextView tvTimeTaken;

    private Button btnViewResults;
    private Button btnBackToDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assessment_complete);

        // Connect Java variables to XML views
        tvScore = findViewById(R.id.tvScore);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvIncorrect = findViewById(R.id.tvIncorrect);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvStrengths = findViewById(R.id.tvStrengths);
        tvAreasImprovement = findViewById(R.id.tvAreasImprovement);
        tvTimeTaken = findViewById(R.id.tvTimeTaken);

        btnViewResults = findViewById(R.id.btnViewResults);
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);


        // Receive the assessment results
        Intent intent = getIntent();

        int correctCount =
                intent.getIntExtra("correctCount", 0);

        int incorrectCount =
                intent.getIntExtra("incorrectCount", 0);

        int percentage =
                intent.getIntExtra("percentage", 0);

        String strengths =
                intent.getStringExtra("strengths");

        String areasImprovement =
                intent.getStringExtra("areasImprovement");

        long timeTakenMillis =
                intent.getLongExtra("timeTakenMillis", 0);


        // Display score
        tvScore.setText(
                correctCount + " / "
                        + (correctCount + incorrectCount)
        );

        tvCorrect.setText(
                "Correct: " + correctCount
        );

        tvIncorrect.setText(
                "Incorrect: " + incorrectCount
        );

        tvPercentage.setText(
                percentage + "%"
        );


        // Display strengths
        if (strengths == null || strengths.trim().isEmpty()) {

            tvStrengths.setText(
                    "No specific strengths identified yet."
            );

        } else {

            tvStrengths.setText(strengths);
        }


        // Display improvement areas
        if (areasImprovement == null
                || areasImprovement.trim().isEmpty()) {

            tvAreasImprovement.setText(
                    "No specific improvement areas identified yet."
            );

        } else {

            tvAreasImprovement.setText(
                    areasImprovement
            );
        }


        // Convert milliseconds into minutes and seconds
        long totalSeconds = timeTakenMillis / 1000;

        long minutes = totalSeconds / 60;

        long seconds = totalSeconds % 60;


        tvTimeTaken.setText(
                String.format(
                        "Time Taken: %02d:%02d",
                        minutes,
                        seconds
                )
        );


        // View detailed results
        btnViewResults.setOnClickListener(v -> {

            Intent resultsIntent = new Intent(
                    AssessmentCompleteActivity.this,
                    AssessmentResultsActivity.class
            );

            resultsIntent.putExtra(
                    "correctCount",
                    correctCount
            );

            resultsIntent.putExtra(
                    "incorrectCount",
                    incorrectCount
            );

            resultsIntent.putExtra(
                    "percentage",
                    percentage
            );

            resultsIntent.putExtra(
                    "strengths",
                    strengths
            );

            resultsIntent.putExtra(
                    "areasImprovement",
                    areasImprovement
            );

            resultsIntent.putExtra(
                    "timeTakenMillis",
                    timeTakenMillis
            );

            startActivity(resultsIntent);
        });


        // Return to Dashboard
        btnBackToDashboard.setOnClickListener(v -> {

            Intent dashboardIntent = new Intent(
                    AssessmentCompleteActivity.this,
                    MainActivity.class
            );

            dashboardIntent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            );

            startActivity(dashboardIntent);

            finish();
        });
    }
}