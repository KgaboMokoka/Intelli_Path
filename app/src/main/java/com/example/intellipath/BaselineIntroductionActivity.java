package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaselineIntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_baseline_introduction);

        // Start baseline assessment button
        Button btnStartBaseline = findViewById(R.id.btnStartBaseline);

        btnStartBaseline.setOnClickListener(v -> {

            Intent intent = new Intent(
                    BaselineIntroductionActivity.this,
                    BaselineTestActivity.class
            );

            startActivity(intent);
        });

        // Cancel button
        TextView tvCancelBaseline = findViewById(R.id.tvCancelBaseline);

        tvCancelBaseline.setOnClickListener(v -> {
            finish();
        });
    }
}