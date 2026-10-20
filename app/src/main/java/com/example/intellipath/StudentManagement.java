package com.example.intellipath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class StudentManagement extends AppCompatActivity {

    private LinearLayout savedStudentsContainer;
    private EditText inputStudentSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_management);

        savedStudentsContainer = findViewById(R.id.savedStudentsContainer);
        inputStudentSearch = findViewById(R.id.inputStudentSearch);

        inputStudentSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.btnAddStudent).setOnClickListener(v -> {
            startActivity(new Intent(
                    StudentManagement.this,
                    AddStudentActivity.class
            ));
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(StudentManagement.this, LoginPage.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navDashboard).setOnClickListener(v ->
                startActivity(new Intent(this, AdminDash.class)));

        findViewById(R.id.navStudents).setOnClickListener(v -> {
            // Already on Student Directory.
        });

        findViewById(R.id.navAssessments).setOnClickListener(v ->
                startActivity(new Intent(this, AssesmentManagement.class)));

        findViewById(R.id.navReports).setOnClickListener(v ->
                startActivity(new Intent(this, FeedbackReports.class)));

        findViewById(R.id.navSettings).setOnClickListener(v ->
                startActivity(new Intent(this, AdminSettings.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedStudents();
        filterStudents(inputStudentSearch.getText().toString());
    }

    private void loadSavedStudents() {
        savedStudentsContainer.removeAllViews();

        SharedPreferences preferences = getSharedPreferences(
                "IntelliPathStudents",
                MODE_PRIVATE
        );

        try {
            JSONArray students = new JSONArray(
                    preferences.getString("students", "[]")
            );

            for (int i = 0; i < students.length(); i++) {
                addStudentCard(students.getJSONObject(i));
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not load saved students.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    private void addStudentCard(JSONObject student) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setGravity(Gravity.TOP);
        card.setBackgroundColor(Color.WHITE);
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardParams.topMargin = dp(12);
        card.setLayoutParams(cardParams);

        String name = student.optString("name", "Student");
        String studentId = student.optString("studentId", "");
        String email = student.optString("email", "");
        String year = student.optString("academicYear", "");
        String goal = student.optString("careerGoal", "");
        String status = student.optString("status", "Active");

        TextView initials = new TextView(this);
        initials.setLayoutParams(new LinearLayout.LayoutParams(
                dp(50), dp(50)
        ));
        initials.setGravity(Gravity.CENTER);
        initials.setText(getInitials(name));
        initials.setTextColor(Color.WHITE);
        initials.setTextSize(13);
        initials.setTypeface(null, Typeface.BOLD);
        initials.setBackgroundColor(Color.rgb(108, 92, 231));
        card.addView(initials);

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );
        card.addView(details, detailsParams);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextColor(Color.rgb(23, 23, 47));
        nameView.setTextSize(14);
        nameView.setTypeface(null, Typeface.BOLD);
        details.addView(nameView);

        TextView idView = new TextView(this);
        idView.setText("ID: " + studentId);
        idView.setTextColor(Color.rgb(119, 119, 138));
        idView.setTextSize(10);
        details.addView(idView);

        TextView emailView = new TextView(this);
        emailView.setText(email);
        emailView.setTextColor(Color.rgb(119, 119, 138));
        emailView.setTextSize(10);
        details.addView(emailView);

        TextView yearView = new TextView(this);
        yearView.setText("YEAR: " + year);
        yearView.setTextColor(Color.rgb(85, 85, 106));
        yearView.setTextSize(10);
        yearView.setPadding(0, dp(8), 0, 0);
        details.addView(yearView);

        TextView goalView = new TextView(this);
        goalView.setText("GOAL: " + goal);
        goalView.setTextColor(Color.rgb(85, 85, 106));
        goalView.setTextSize(10);
        details.addView(goalView);

        TextView statusView = new TextView(this);
        statusView.setText("● " + status);
        statusView.setTextSize(10);
        statusView.setPadding(0, dp(8), 0, 0);

        if (status.equalsIgnoreCase("Active")) {
            statusView.setTextColor(Color.rgb(46, 139, 87));
        } else if (status.equalsIgnoreCase("At Risk")) {
            statusView.setTextColor(Color.rgb(217, 145, 45));
        } else {
            statusView.setTextColor(Color.GRAY);
        }

        details.addView(statusView);
        savedStudentsContainer.addView(card);
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isEmpty()) {
            return "ST";
        }

        if (parts.length == 1) {
            return parts[0].substring(
                    0, Math.min(2, parts[0].length())
            ).toUpperCase();
        }

        return (parts[0].substring(0, 1) +
                parts[parts.length - 1].substring(0, 1))
                .toUpperCase();
    }

    private void filterStudents(String query) {
        String search = query.trim().toLowerCase();

        // Filter newly saved students.
        for (int i = 0; i < savedStudentsContainer.getChildCount(); i++) {
            View card = savedStudentsContainer.getChildAt(i);
            String cardText = getAllText(card).toLowerCase();

            card.setVisibility(
                    cardText.contains(search) ? View.VISIBLE : View.GONE
            );
        }

        // Filter the original example student cards.
        ScrollView scrollView = findStudentScrollView();

        if (scrollView == null) {
            return;
        }

        View rootView = scrollView.getChildAt(0);

        if (!(rootView instanceof LinearLayout)) {
            return;
        }

        LinearLayout root = (LinearLayout) rootView;

        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);

            // Skip the saved-student container and bottom navigation.
            if (child == savedStudentsContainer ||
                    child.findViewById(R.id.navDashboard) != null) {
                continue;
            }

            String cardText = getAllText(child).toLowerCase();

            if (cardText.contains("kgabo mokoka") ||
                    cardText.contains("boitumelo kgasi") ||
                    cardText.contains("hlonipha baloyi")) {

                child.setVisibility(
                        cardText.contains(search) ? View.VISIBLE : View.GONE
                );
            }
        }
    }

    private String getAllText(View view) {
        StringBuilder text = new StringBuilder();

        if (view instanceof TextView) {
            text.append(((TextView) view).getText()).append(" ");
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                text.append(getAllText(group.getChildAt(i))).append(" ");
            }
        }

        return text.toString();
    }

    private ScrollView findStudentScrollView() {
        View content = findViewById(android.R.id.content);

        if (content instanceof ViewGroup) {
            return findScrollView((ViewGroup) content);
        }

        return null;
    }

    private ScrollView findScrollView(ViewGroup group) {
        if (group instanceof ScrollView) {
            return (ScrollView) group;
        }

        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);

            if (child instanceof ViewGroup) {
                ScrollView result = findScrollView((ViewGroup) child);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}