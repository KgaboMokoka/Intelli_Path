package com.example.intellipath;

import android.app.AlertDialog;
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
import android.widget.Button;
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

    private static final String PREFS_NAME = "IntelliPathStudents";
    private static final String STUDENTS_KEY = "students";
    private static final String DELETED_SAMPLES_KEY = "deletedSampleStudents";

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

        findViewById(R.id.btnAddStudent).setOnClickListener(v ->
                startActivity(new Intent(this, AddStudentActivity.class)));

        findViewById(R.id.btnDeleteKgabo).setOnClickListener(v ->
                confirmDeleteSample("Kgabo Mokoka"));

        findViewById(R.id.btnDeleteBoitumelo).setOnClickListener(v ->
                confirmDeleteSample("Boitumelo Kgasi"));

        findViewById(R.id.btnDeleteHlonipha).setOnClickListener(v ->
                confirmDeleteSample("Hlonipha Baloyi"));

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.navDashboard).setOnClickListener(v ->
                startActivity(new Intent(this, AdminDash.class)));

        findViewById(R.id.navStudents).setOnClickListener(v -> {
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
        applyDeletedSampleStudents();
        filterStudents(inputStudentSearch.getText().toString());
    }

    private void loadSavedStudents() {
        savedStudentsContainer.removeAllViews();

        SharedPreferences preferences = getSharedPreferences(
                PREFS_NAME, MODE_PRIVATE);

        try {
            JSONArray students = new JSONArray(
                    preferences.getString(STUDENTS_KEY, "[]"));

            for (int i = 0; i < students.length(); i++) {
                addStudentCard(students.getJSONObject(i));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Could not load saved students.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private void addStudentCard(JSONObject student) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setGravity(Gravity.TOP);
        card.setBackgroundColor(Color.WHITE);
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.topMargin = dp(12);
        card.setLayoutParams(cardParams);

        String name = student.optString("name", "Student");
        String studentId = student.optString("studentId", "");
        String email = student.optString("email", "");
        String year = student.optString("academicYear", "");
        String goal = student.optString("careerGoal", "");
        String status = student.optString("status", "Active");

        TextView initials = new TextView(this);
        initials.setLayoutParams(new LinearLayout.LayoutParams(dp(50), dp(50)));
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

        card.addView(details, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        addDetail(details, name, true);
        addDetail(details, "ID: " + studentId, false);
        addDetail(details, email, false);
        addDetail(details, "YEAR: " + year, false);
        addDetail(details, "GOAL: " + goal, false);

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

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setTextSize(10);
        deleteButton.setTextColor(Color.WHITE);
        deleteButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(217, 83, 79)));

        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                dp(70), dp(40));
        deleteParams.leftMargin = dp(6);
        deleteButton.setLayoutParams(deleteParams);

        deleteButton.setOnClickListener(v ->
                confirmDeleteSavedStudent(studentId, name));

        card.addView(deleteButton);
        savedStudentsContainer.addView(card);
    }

    private void addDetail(LinearLayout container, String value, boolean bold) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextColor(bold ? Color.rgb(23, 23, 47) :
                Color.rgb(119, 119, 138));
        text.setTextSize(bold ? 14 : 10);

        if (bold) {
            text.setTypeface(null, Typeface.BOLD);
        }

        container.addView(text);
    }

    private String getInitials(String name) {
        String[] parts = name.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isEmpty()) {
            return "ST";
        }

        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length()))
                    .toUpperCase();
        }

        return (parts[0].substring(0, 1) +
                parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private void confirmDeleteSavedStudent(String studentId, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to permanently delete "
                        + name + "?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    SharedPreferences preferences = getSharedPreferences(
                            PREFS_NAME, MODE_PRIVATE);

                    try {
                        JSONArray students = new JSONArray(
                                preferences.getString(STUDENTS_KEY, "[]"));
                        JSONArray updatedStudents = new JSONArray();
                        boolean deleted = false;

                        for (int i = 0; i < students.length(); i++) {
                            JSONObject student = students.getJSONObject(i);

                            boolean matches = student.optString("studentId")
                                    .equalsIgnoreCase(studentId);

                            if (matches && !deleted) {
                                deleted = true;
                            } else {
                                updatedStudents.put(student);
                            }
                        }

                        if (deleted) {
                            preferences.edit()
                                    .putString(STUDENTS_KEY,
                                            updatedStudents.toString())
                                    .apply();

                            loadSavedStudents();
                            applyDeletedSampleStudents();
                            filterStudents(inputStudentSearch.getText().toString());

                            Toast.makeText(this, name + " deleted successfully.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Student was not found.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Could not delete student.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void confirmDeleteSample(String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to permanently delete "
                        + name + "?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    SharedPreferences preferences = getSharedPreferences(
                            PREFS_NAME, MODE_PRIVATE);

                    try {
                        JSONArray deletedStudents = new JSONArray(
                                preferences.getString(DELETED_SAMPLES_KEY, "[]"));

                        boolean alreadyDeleted = false;

                        for (int i = 0; i < deletedStudents.length(); i++) {
                            if (deletedStudents.getString(i).equalsIgnoreCase(name)) {
                                alreadyDeleted = true;
                                break;
                            }
                        }

                        if (!alreadyDeleted) {
                            deletedStudents.put(name);
                        }

                        preferences.edit()
                                .putString(DELETED_SAMPLES_KEY,
                                        deletedStudents.toString())
                                .apply();

                        applyDeletedSampleStudents();
                        filterStudents(inputStudentSearch.getText().toString());

                        Toast.makeText(this, name + " deleted permanently.",
                                Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(this, "Could not delete student.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void applyDeletedSampleStudents() {
        SharedPreferences preferences = getSharedPreferences(
                PREFS_NAME, MODE_PRIVATE);

        try {
            JSONArray deletedStudents = new JSONArray(
                    preferences.getString(DELETED_SAMPLES_KEY, "[]"));

            ScrollView scrollView = findStudentScrollView();

            if (scrollView == null || scrollView.getChildCount() == 0) {
                return;
            }

            View rootView = scrollView.getChildAt(0);

            if (!(rootView instanceof LinearLayout)) {
                return;
            }

            LinearLayout root = (LinearLayout) rootView;

            for (int i = root.getChildCount() - 1; i >= 0; i--) {
                View child = root.getChildAt(i);

                if (child == savedStudentsContainer ||
                        child.findViewById(R.id.navDashboard) != null) {
                    continue;
                }

                String text = getAllText(child).toLowerCase();

                for (int j = 0; j < deletedStudents.length(); j++) {
                    String deletedName = deletedStudents.getString(j)
                            .toLowerCase();

                    if (text.contains(deletedName)) {
                        root.removeView(child);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterStudents(String query) {
        String search = query.trim().toLowerCase();

        for (int i = 0; i < savedStudentsContainer.getChildCount(); i++) {
            View card = savedStudentsContainer.getChildAt(i);
            card.setVisibility(getAllText(card).toLowerCase().contains(search)
                    ? View.VISIBLE : View.GONE);
        }

        ScrollView scrollView = findStudentScrollView();

        if (scrollView == null || scrollView.getChildCount() == 0) {
            return;
        }

        View rootView = scrollView.getChildAt(0);

        if (!(rootView instanceof LinearLayout)) {
            return;
        }

        LinearLayout root = (LinearLayout) rootView;

        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);

            if (child == savedStudentsContainer ||
                    child.findViewById(R.id.navDashboard) != null) {
                continue;
            }

            String text = getAllText(child).toLowerCase();

            if (text.contains("kgabo mokoka") ||
                    text.contains("boitumelo kgasi") ||
                    text.contains("hlonipha baloyi")) {
                child.setVisibility(text.contains(search)
                        ? View.VISIBLE : View.GONE);
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
