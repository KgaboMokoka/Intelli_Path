
package com.example.intellipath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateAssessmentActivity extends AppCompatActivity {

    private EditText inputTitle, inputDescription, inputTimeLimit;
    private Spinner spinnerDifficulty;
    private LinearLayout questionsContainer;
    private final List<QuestionData> questions = new ArrayList<>();

    private final int navy = Color.rgb(21, 28, 82);
    private final int purple = Color.rgb(233, 232, 248);
    private final int darkText = Color.rgb(37, 41, 68);

    private static class QuestionData {
        EditText questionInput;
        Spinner typeSpinner;
        LinearLayout answerContainer;
        EditText expectedAnswer;
        RadioGroup correctAnswerGroup;
        RadioGroup trueFalseGroup;
        final List<EditText> options = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assessment);

        inputTitle = findViewById(R.id.inputAssessmentTitle);
        inputDescription = findViewById(R.id.inputAssessmentDescription);
        inputTimeLimit = findViewById(R.id.inputTimeLimit);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        questionsContainer = findViewById(R.id.questionsContainer);

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Beginner", "Intermediate", "Advanced"}
        );
        difficultyAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerDifficulty.setAdapter(difficultyAdapter);

        findViewById(R.id.btnBackAssessments).setOnClickListener(v -> {
            Intent intent = new Intent(
                    CreateAssessmentActivity.this,
                    AssesmentManagement.class
            );
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnAddQuestion).setOnClickListener(v -> addQuestion());

        findViewById(R.id.btnSaveDraft).setOnClickListener(
                v -> saveAssessment("Draft")
        );

        findViewById(R.id.btnPublishAssessment).setOnClickListener(
                v -> saveAssessment("Published")
        );

        addQuestion();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private TextView label(String text) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(darkText);
        view.setTextSize(13);
        view.setTypeface(null, Typeface.BOLD);
        view.setPadding(0, dp(12), 0, dp(5));
        return view;
    }

    private EditText input(String hint) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setTextColor(darkText);
        edit.setTextSize(14);
        edit.setPadding(dp(10), dp(6), dp(10), dp(6));
        return edit;
    }

    private Button button(String text, boolean primary) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextColor(primary ? Color.WHITE : navy);
        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        primary ? navy : purple
                )
        );
        return button;
    }

    private void addQuestion() {
        QuestionData data = new QuestionData();

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(14));
        card.setBackgroundColor(Color.WHITE);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dp(16);
        card.setLayoutParams(cardParams);

        TextView heading = label("Question " + (questions.size() + 1));
        heading.setTextSize(17);
        heading.setTextColor(navy);
        card.addView(heading);

        card.addView(label("Question Type"));

        data.typeSpinner = new Spinner(this);
        String[] types = {
                "Multiple choice",
                "Text answer",
                "True / False",
                "Code question"
        };

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                types
        );
        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        data.typeSpinner.setAdapter(typeAdapter);
        card.addView(data.typeSpinner);

        card.addView(label("Question"));
        data.questionInput = input("Enter your question");
        data.questionInput.setMinHeight(dp(55));
        card.addView(data.questionInput);

        data.answerContainer = new LinearLayout(this);
        data.answerContainer.setOrientation(LinearLayout.VERTICAL);
        card.addView(data.answerContainer);

        Button remove = button("Remove Question", false);
        remove.setOnClickListener(v -> {
            questions.remove(data);
            questionsContainer.removeView(card);
            updateQuestionNumbers();
        });
        card.addView(remove);

        questions.add(data);
        questionsContainer.addView(card);

        data.typeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        buildAnswerFields(data);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        buildAnswerFields(data);
    }

    private void buildAnswerFields(QuestionData data) {
        data.answerContainer.removeAllViews();
        data.options.clear();
        data.expectedAnswer = null;
        data.correctAnswerGroup = null;
        data.trueFalseGroup = null;

        String type = data.typeSpinner.getSelectedItem().toString();

        if (type.equals("Multiple choice")) {
            data.answerContainer.addView(label("Answer Options"));

            data.correctAnswerGroup = new RadioGroup(this);
            data.correctAnswerGroup.setOrientation(RadioGroup.VERTICAL);

            LinearLayout optionsContainer = new LinearLayout(this);
            optionsContainer.setOrientation(LinearLayout.VERTICAL);
            data.answerContainer.addView(optionsContainer);

            for (int i = 0; i < 4; i++) {
                addOption(data, optionsContainer);
            }

            Button addOption = button("+ Add Option", false);
            addOption.setOnClickListener(v -> addOption(data, optionsContainer));
            data.answerContainer.addView(addOption);

            data.answerContainer.addView(label("Select Correct Answer"));
            data.answerContainer.addView(data.correctAnswerGroup);

        } else if (type.equals("True / False")) {
            data.answerContainer.addView(label("Correct Answer"));

            data.trueFalseGroup = new RadioGroup(this);
            data.trueFalseGroup.setOrientation(RadioGroup.VERTICAL);

            RadioButton trueButton = new RadioButton(this);
            trueButton.setText("True");
            trueButton.setTextColor(darkText);
            data.trueFalseGroup.addView(trueButton);

            RadioButton falseButton = new RadioButton(this);
            falseButton.setText("False");
            falseButton.setTextColor(darkText);
            data.trueFalseGroup.addView(falseButton);

            data.answerContainer.addView(data.trueFalseGroup);

        } else {
            data.answerContainer.addView(label(
                    type.equals("Code question")
                            ? "Expected Code / Marking Criteria"
                            : "Expected Answer / Marking Criteria"
            ));

            data.expectedAnswer = input(
                    type.equals("Code question")
                            ? "Enter expected code or marking criteria"
                            : "Enter the expected answer"
            );
            data.expectedAnswer.setMinHeight(dp(65));
            data.expectedAnswer.setGravity(Gravity.TOP);
            data.answerContainer.addView(data.expectedAnswer);
        }
    }

    private void addOption(QuestionData data, LinearLayout optionsContainer) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        EditText option = input("Option " + (char) ('A' + data.options.size()));
        LinearLayout.LayoutParams optionParams = new LinearLayout.LayoutParams(
                0, dp(52), 1
        );
        row.addView(option, optionParams);

        Button remove = button("×", false);
        row.addView(remove);
        optionsContainer.addView(row);

        data.options.add(option);
        refreshCorrectAnswers(data);

        remove.setOnClickListener(v -> {
            if (data.options.size() <= 2) {
                Toast.makeText(
                        this,
                        "Keep at least two options.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            data.options.remove(option);
            optionsContainer.removeView(row);
            refreshCorrectAnswers(data);
            renumberOptions(data);
        });
    }

    private void refreshCorrectAnswers(QuestionData data) {
        if (data.correctAnswerGroup == null) return;

        EditText selectedOption = null;
        int checkedId = data.correctAnswerGroup.getCheckedRadioButtonId();

        if (checkedId != -1) {
            RadioButton checked = data.correctAnswerGroup.findViewById(checkedId);
            if (checked != null) {
                selectedOption = (EditText) checked.getTag();
            }
        }

        data.correctAnswerGroup.removeAllViews();

        for (int i = 0; i < data.options.size(); i++) {
            RadioButton radio = new RadioButton(this);
            radio.setText("Option " + (char) ('A' + i));
            radio.setTextColor(darkText);
            radio.setTag(data.options.get(i));
            data.correctAnswerGroup.addView(radio);

            if (data.options.get(i) == selectedOption) {
                radio.setChecked(true);
            }
        }
    }

    private void renumberOptions(QuestionData data) {
        LinearLayout optionsContainer = (LinearLayout) data.answerContainer.getChildAt(1);

        for (int i = 0; i < data.options.size(); i++) {
            LinearLayout row = (LinearLayout) optionsContainer.getChildAt(i);
            EditText option = data.options.get(i);
            option.setHint("Option " + (char) ('A' + i));
        }

        refreshCorrectAnswers(data);
    }

    private void updateQuestionNumbers() {
        for (int i = 0; i < questionsContainer.getChildCount(); i++) {
            LinearLayout card = (LinearLayout) questionsContainer.getChildAt(i);
            TextView heading = (TextView) card.getChildAt(0);
            heading.setText("Question " + (i + 1));
        }
    }

    private void saveAssessment(String status) {
        String title = inputTitle.getText().toString().trim();

        if (title.isEmpty()) {
            inputTitle.setError("Enter an assessment title");
            inputTitle.requestFocus();
            return;
        }

        if (questions.isEmpty()) {
            Toast.makeText(this, "Add at least one question.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray questionArray = new JSONArray();

        try {
            for (int i = 0; i < questions.size(); i++) {
                QuestionData data = questions.get(i);
                String prompt = data.questionInput.getText().toString().trim();
                String type = data.typeSpinner.getSelectedItem().toString();

                if (prompt.isEmpty()) {
                    data.questionInput.setError("Enter the question");
                    data.questionInput.requestFocus();
                    return;
                }

                JSONObject question = new JSONObject();
                question.put("type", type);
                question.put("question", prompt);

                if (type.equals("Multiple choice")) {
                    JSONArray options = new JSONArray();

                    for (EditText option : data.options) {
                        String value = option.getText().toString().trim();
                        if (value.isEmpty()) {
                            option.setError("Enter an option");
                            option.requestFocus();
                            return;
                        }
                        options.put(value);
                    }

                    int checkedId = data.correctAnswerGroup.getCheckedRadioButtonId();

                    if (checkedId == -1) {
                        Toast.makeText(
                                this,
                                "Choose the correct answer for Question " + (i + 1),
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    RadioButton selected =
                            data.correctAnswerGroup.findViewById(checkedId);

                    int correctIndex = data.options.indexOf(
                            (EditText) selected.getTag()
                    );

                    question.put("options", options);
                    question.put("correctAnswerIndex", correctIndex);

                } else if (type.equals("True / False")) {
                    int checkedId = data.trueFalseGroup.getCheckedRadioButtonId();

                    if (checkedId == -1) {
                        Toast.makeText(
                                this,
                                "Choose True or False for Question " + (i + 1),
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    RadioButton selected =
                            data.trueFalseGroup.findViewById(checkedId);
                    question.put("correctAnswer", selected.getText().toString());

                } else {
                    String expected = data.expectedAnswer.getText().toString().trim();

                    if (expected.isEmpty()) {
                        data.expectedAnswer.setError("Enter the expected answer");
                        data.expectedAnswer.requestFocus();
                        return;
                    }

                    question.put("expectedAnswer", expected);
                }

                questionArray.put(question);
            }

            JSONObject assessment = new JSONObject();
            assessment.put("title", title);
            assessment.put("description",
                    inputDescription.getText().toString().trim());
            assessment.put("difficulty",
                    spinnerDifficulty.getSelectedItem().toString());
            assessment.put("timeLimit",
                    inputTimeLimit.getText().toString().trim());
            assessment.put("status", status);
            assessment.put("questionCount", questions.size());
            assessment.put("questions", questionArray);

            SharedPreferences preferences = getSharedPreferences(
                    "IntelliPathAssessments",
                    MODE_PRIVATE
            );

            JSONArray saved = new JSONArray(
                    preferences.getString("assessments", "[]")
            );

            saved.put(assessment);

            preferences.edit()
                    .putString("assessments", saved.toString())
                    .apply();

            Toast.makeText(
                    this,
                    status.equals("Published")
                            ? "Assessment published!"
                            : "Draft saved!",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    CreateAssessmentActivity.this,
                    AssesmentManagement.class
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not save assessment: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}

