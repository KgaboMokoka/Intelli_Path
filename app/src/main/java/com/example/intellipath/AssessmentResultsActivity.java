package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AssessmentResultsActivity extends AppCompatActivity {

    private TextView tvScore;
    private TextView tvPercentage;
    private TextView tvCorrect;
    private TextView tvIncorrect;
    private TextView tvStrengths;
    private TextView tvAreasImprovement;
    private TextView tvTimeTaken;

    private Button btnBackToDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assessment_results);

        // Connect Java variables to XML views
        tvScore = findViewById(R.id.tvScore);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvIncorrect = findViewById(R.id.tvIncorrect);
        tvStrengths = findViewById(R.id.tvStrengths);
        tvAreasImprovement = findViewById(R.id.tvAreasImprovement);
        tvTimeTaken = findViewById(R.id.tvTimeTaken);

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
        int totalQuestions =
                correctCount + incorrectCount;

        tvScore.setText(
                correctCount + " / " + totalQuestions
        );

        tvPercentage.setText(
                percentage + "%"
        );

        tvCorrect.setText(
                "Correct: " + correctCount
        );

        tvIncorrect.setText(
                "Incorrect: " + incorrectCount
        );

        // Display strengths
        if (strengths == null || strengths.trim().isEmpty()) {

            tvStrengths.setText(
                    "No specific strengths identified."
            );

        } else {

            tvStrengths.setText(strengths);
        }

        // Display areas for improvement
        if (areasImprovement == null
                || areasImprovement.trim().isEmpty()) {

            tvAreasImprovement.setText(
                    "No specific improvement areas identified."
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

        // Boity edit: Open the personalised roadmap overview
        // and pass the assessment percentage to it.
        btnBackToDashboard.setText("Generate Roadmap");

        btnBackToDashboard.setOnClickListener(v -> {

            Intent roadmapIntent = new Intent(
                    AssessmentResultsActivity.this,
                    GeneratedRoadmapActivity.class
            );

            // Boity edit: Pass the assessment percentage
            // to GeneratedRoadmapActivity.
            roadmapIntent.putExtra("percentage", percentage);

            startActivity(roadmapIntent);

            finish();
        });
    }
}