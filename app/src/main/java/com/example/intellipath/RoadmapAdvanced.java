package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RoadmapAdvanced extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmap_advanced);

        findViewById(R.id.card_01).setOnClickListener(v -> {
            Intent intent = new Intent(RoadmapAdvanced.this, LabIntroductionActivity.class);
            intent.putExtra("lab_id", "9d0db07d-84c0-4b09-bb69-267ef11ac2bd");
            startActivity(intent);
        });

        TextView backToDashboard =
                findViewById(R.id.btn_back_dashboard);

        backToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(RoadmapAdvanced.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
