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

        TextView backToDashboard =
                findViewById(R.id.btn_back_dashboard);

        backToDashboard.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RoadmapAdvanced.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}
