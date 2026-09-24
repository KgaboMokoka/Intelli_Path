package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LabIntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lab_introduction);

        String labId = getIntent().getStringExtra("lab_id");

        // Start lab simulation button
        Button btnStartLab = findViewById(R.id.btnStartLab);

        btnStartLab.setOnClickListener(v -> {

            if (labId == null) {
                Toast.makeText(LabIntroductionActivity.this,
                        "Missing lab info — please go back and try again.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(
                    LabIntroductionActivity.this,
                    LabSimulationActivity.class
            );

            intent.putExtra("lab_id", labId);

            startActivity(intent);
        });

        // Cancel button
        TextView tvCancelLab = findViewById(R.id.tvCancelLab);

        tvCancelLab.setOnClickListener(v -> {
            finish();
        });
    }
}