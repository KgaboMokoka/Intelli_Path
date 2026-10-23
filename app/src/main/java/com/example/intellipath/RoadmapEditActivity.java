
package com.example.intellipath;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoadmapEditActivity extends AppCompatActivity {

    private EditText inputTitle, inputDescription, inputModules;
    private EditText inputLabs, inputLevel, inputScoreRange;
    private String roadmapKey;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadmap_edit);

        preferences = getSharedPreferences("IntelliPathRoadmaps", MODE_PRIVATE);

        inputTitle = findViewById(R.id.inputRoadmapTitle);
        inputDescription = findViewById(R.id.inputRoadmapDescription);
        inputModules = findViewById(R.id.inputRoadmapModules);
        inputLabs = findViewById(R.id.inputRoadmapLabs);
        inputLevel = findViewById(R.id.inputRoadmapLevel);
        inputScoreRange = findViewById(R.id.inputRoadmapScoreRange);

        TextView pageTitle = findViewById(R.id.txtEditRoadmapHeading);
        Button btnSave = findViewById(R.id.btnSaveRoadmap);
        Button btnCancel = findViewById(R.id.btnCancelRoadmap);

        roadmapKey = getIntent().getStringExtra("roadmapKey");

        if (roadmapKey == null) {
            Toast.makeText(this, "Roadmap not selected.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pageTitle.setText("Edit " + getDefaultTitle());

        loadRoadmap();

        btnSave.setOnClickListener(v -> saveRoadmap());

        btnCancel.setOnClickListener(v -> finish());
    }

    private String getDefaultTitle() {
        switch (roadmapKey) {
            case "beginner":
                return "Beginner Roadmap";
            case "intermediate":
                return "Intermediate Roadmap";
            case "advanced":
                return "Advanced Roadmap";
            default:
                return "Roadmap";
        }
    }

    private String getDefaultDescription() {
        switch (roadmapKey) {
            case "beginner":
                return "Foundation Backend Developer";
            case "intermediate":
                return "Backend Developer";
            case "advanced":
                return "Advanced Backend Developer";
            default:
                return "";
        }
    }

    private String getDefaultModules() {
        switch (roadmapKey) {
            case "beginner":
                return "6";
            case "intermediate":
                return "8";
            case "advanced":
                return "10";
            default:
                return "0";
        }
    }

    private String getDefaultLabs() {
        switch (roadmapKey) {
            case "beginner":
                return "4";
            case "intermediate":
                return "5";
            case "advanced":
                return "6";
            default:
                return "0";
        }
    }

    private String getDefaultLevel() {
        return roadmapKey.substring(0, 1).toUpperCase()
                + roadmapKey.substring(1);
    }

    private String getDefaultScoreRange() {
        switch (roadmapKey) {
            case "beginner":
                return "0-49%";
            case "intermediate":
                return "50-74%";
            case "advanced":
                return "75-100%";
            default:
                return "";
        }
    }

    private void loadRoadmap() {
        inputTitle.setText(preferences.getString(
                roadmapKey + "_title", getDefaultTitle()));

        inputDescription.setText(preferences.getString(
                roadmapKey + "_description", getDefaultDescription()));

        inputModules.setText(preferences.getString(
                roadmapKey + "_modules", getDefaultModules()));

        inputLabs.setText(preferences.getString(
                roadmapKey + "_labs", getDefaultLabs()));

        inputLevel.setText(preferences.getString(
                roadmapKey + "_level", getDefaultLevel()));

        inputScoreRange.setText(preferences.getString(
                roadmapKey + "_scoreRange", getDefaultScoreRange()));
    }

    private void saveRoadmap() {
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        String modules = inputModules.getText().toString().trim();
        String labs = inputLabs.getText().toString().trim();
        String level = inputLevel.getText().toString().trim();
        String scoreRange = inputScoreRange.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()
                || modules.isEmpty() || labs.isEmpty()
                || level.isEmpty() || scoreRange.isEmpty()) {
            Toast.makeText(this, "Please complete all fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int moduleCount = Integer.parseInt(modules);
            int labCount = Integer.parseInt(labs);

            if (moduleCount < 0 || labCount < 0) {
                Toast.makeText(this, "Modules and labs cannot be negative.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Modules and labs must be whole numbers.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        preferences.edit()
                .putString(roadmapKey + "_title", title)
                .putString(roadmapKey + "_description", description)
                .putString(roadmapKey + "_modules", modules)
                .putString(roadmapKey + "_labs", labs)
                .putString(roadmapKey + "_level", level)
                .putString(roadmapKey + "_scoreRange", scoreRange)
                .apply();

        Toast.makeText(this, "Roadmap changes saved.",
                Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }
}
