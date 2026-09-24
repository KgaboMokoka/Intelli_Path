package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LabResultsActivity extends AppCompatActivity {

    private TextView tvScoreBand;
    private TextView tvFeedback;

    private Button btnBackToDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lab_results);

        // Connect Java variables to XML views
        tvScoreBand = findViewById(R.id.tvScoreBand);
        tvFeedback = findViewById(R.id.tvFeedback);

        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);


        // Receive the lab result
        Intent intent = getIntent();

        String scoreBand =
                intent.getStringExtra("scoreBand");

        String feedback =
                intent.getStringExtra("feedback");


        // Display score band
        if (scoreBand == null || scoreBand.trim().isEmpty()) {

            tvScoreBand.setText(
                    "Result unavailable"
            );

        } else {

            tvScoreBand.setText(scoreBand);
        }


        // Display feedback
        if (feedback == null || feedback.trim().isEmpty()) {

            tvFeedback.setText(
                    "No specific feedback for this attempt."
            );

        } else {

            tvFeedback.setText(feedback);
        }


        // Return to Dashboard
        btnBackToDashboard.setOnClickListener(v -> {

            Intent dashboardIntent = new Intent(
                    LabResultsActivity.this,
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