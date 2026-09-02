package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class RegistrationTwo extends AppCompatActivity {

    private Spinner campusDropdown, currentAcademicYearDropDown;
    private String selectedCampus = "";
    private String selectedAcademicYear = "";
    private String selectedCareerGoal = "";
    private Button registrationContinueButton;

    private List<TextView> careerGoalTiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_two);

        campusDropdown = findViewById(R.id.campusDropdown);
        currentAcademicYearDropDown = findViewById(R.id.currentAcademicYearDropDown);

        setupCampusDropdown();
        setupAcademicYearDropdown();
        setupCareerGoalTiles();
        registrationContinueButton = findViewById(R.id.registrationContinueButton);
        findViewById(R.id.registrationContinueButton)
                .setOnClickListener(view -> finishRegistration());
    }

    private void setupCampusDropdown() {
        String[] campuses = {
                "Select your campus",
                "Eduvos Bedford",
                "Eduvos Midrand",
                "Eduvos Pretoria",
                "Eduvos Vaal",
                "Eduvos Durban",
                "Eduvos Mbombela",
                "Eduvos Potchefstroom",
                "Eduvos Mowbray",
                "Eduvos Tygervalley",
                "Eduvos Bloemfontein",
                "Eduvos East London",
                "Eduvos Nelson Mandela Bay"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                campuses
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        campusDropdown.setAdapter(adapter);

        campusDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id
            ) {
                if (position == 0) {
                    selectedCampus = "";
                } else {
                    selectedCampus = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCampus = "";
            }
        });
    }

    private void setupAcademicYearDropdown() {
        String[] years = {
                "Select your academic year",
                "First Year",
                "Second Year",
                "Third Year",
                "Honours",
                "Masters (MSc)",
                "Doctorate (PhD)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        currentAcademicYearDropDown.setAdapter(adapter);

        currentAcademicYearDropDown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        if (position == 0) {
                            selectedAcademicYear = "";
                        } else {
                            selectedAcademicYear =
                                    parent.getItemAtPosition(position).toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedAcademicYear = "";
                    }
                });
    }

    private void setupCareerGoalTiles() {
        careerGoalTiles = Arrays.asList(
                findViewById(R.id.goalFullStack),
                findViewById(R.id.goalFrontEnd),
                findViewById(R.id.goalBackEnd),
                findViewById(R.id.goalAI),
                findViewById(R.id.goalData),
                findViewById(R.id.goalCloud)
        );

        for (TextView tile : careerGoalTiles) {
            tile.setOnClickListener(view -> {
                for (TextView t : careerGoalTiles) {
                    t.setSelected(t == view);
                }
                selectedCareerGoal = ((TextView) view).getText().toString();
            });
        }
    }

    private void finishRegistration() {
        if (selectedCampus.isEmpty()) {
            Toast.makeText(this, "Please select your campus", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedAcademicYear.isEmpty()) {
            Toast.makeText(this, "Please select your academic year", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCareerGoal.isEmpty()) {
            Toast.makeText(this, "Please select a career goal", Toast.LENGTH_SHORT).show();
            return;
        }

        sendDataToSupabase();

        startActivity(new Intent(RegistrationTwo.this, MainActivity.class));
        finish();
    }

    private void sendDataToSupabase() {
        // since we have two different phases registrationTwo should update the authenticated user.
        // so remember that for when implementing this code.
    }
}

