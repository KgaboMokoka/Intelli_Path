package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intellipath.data.LabSimulationRepository;
import com.example.intellipath.data.LabStep;
import com.example.intellipath.data.LabStepOption;
import com.example.intellipath.data.StudentLabAttempt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.intellipath.data.NewLabResult;
import kotlin.Unit;

public class LabSimulationActivity extends AppCompatActivity {

    private static final String STEP_SINGLE_SELECT = "single_select";
    private static final String STEP_MULTI_SELECT = "multi_select";
    private static final String STEP_IMAGE_SELECT = "image_select";

    public static final String EXTRA_LAB_ID = "lab_id";

    // ---- Views ----
    private TextView tvStepNumber;
    private ProgressBar stepProgress;
    private TextView tvPrompt;
    private LinearLayout choiceContainer;
    private EditText etJustification;
    private Button btnPrevious;
    private Button btnNext;

    // ---- State ----
    private String labId;
    private String studentId;
    private String attemptId;

    private List<LabStep> steps = new ArrayList<>();
    private int currentStepIndex = 0;

    // stepId -> options for that step (avoid re-fetching on Previous/Next)
    private final Map<String, List<LabStepOption>> optionsCache = new HashMap<>();

    // step index -> the option(s) the student picked at that step
    private final Map<Integer, List<LabStepOption>> selections = new HashMap<>();

    // step index -> justification text typed at that step
    private final Map<Integer, String> justifications = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lab_simulation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();

        labId = getIntent().getStringExtra(EXTRA_LAB_ID);
        studentId = LabSimulationRepository.getCurrentStudentId();

        if (labId == null || studentId == null) {
            Toast.makeText(this, "Unable to start lab — missing lab or student info.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnPrevious.setOnClickListener(v -> goToPreviousStep());
        btnNext.setOnClickListener(v -> onNextClicked());

        startBootstrap();
    }

    private void bindViews() {
        tvStepNumber = findViewById(R.id.tvStepNumber);
        stepProgress = findViewById(R.id.stepProgress);
        tvPrompt = findViewById(R.id.tvPrompt);
        choiceContainer = findViewById(R.id.choiceContainer);
        etJustification = findViewById(R.id.etJustification);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
    }

    private void startBootstrap() {
        LabSimulationRepository.startAttempt(studentId, labId, new LabSimulationRepository.Callback<StudentLabAttempt>() {
            @Override
            public void onSuccess(StudentLabAttempt result) {
                attemptId = result.getAttempt_id();
                loadSteps();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(LabSimulationActivity.this, "Couldn't start attempt: " + message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void loadSteps() {
        LabSimulationRepository.fetchSteps(labId, new LabSimulationRepository.Callback<List<LabStep>>() {
            @Override
            public void onSuccess(List<LabStep> result) {
                steps = result;
                if (steps.isEmpty()) {
                    Toast.makeText(LabSimulationActivity.this, "This lab has no steps configured.", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                currentStepIndex = 0;
                renderStep(currentStepIndex);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(LabSimulationActivity.this, "Couldn't load steps: " + message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    // ---- Stage 2 will fill these in ----
    private void renderStep(int index) {
        currentStepIndex = index;
        LabStep step = steps.get(index);

        tvStepNumber.setText("Step " + (index + 1) + " of " + steps.size());
        stepProgress.setMax(steps.size());
        stepProgress.setProgress(index + 1);
        tvPrompt.setText(step.getPrompt());

        btnPrevious.setEnabled(index > 0);
        btnNext.setText(index == steps.size() - 1 ? "Submit" : "Next");

        etJustification.setVisibility(step.getRequires_justification() ? View.VISIBLE : View.GONE);
        String savedJustification = justifications.get(index);
        etJustification.setText(savedJustification != null ? savedJustification : "");

        List<LabStepOption> cachedOptions = optionsCache.get(step.getStep_id());
        if (cachedOptions != null) {
            buildChoiceViews(step, cachedOptions);
            return;
        }

        choiceContainer.removeAllViews();
        LabSimulationRepository.fetchOptions(step.getStep_id(), new LabSimulationRepository.Callback<List<LabStepOption>>() {
            @Override
            public void onSuccess(List<LabStepOption> result) {
                optionsCache.put(step.getStep_id(), result);
                // Guard against a stale callback landing after the user has
                // already navigated to a different step while this was in flight.
                if (currentStepIndex == index) {
                    buildChoiceViews(step, result);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(LabSimulationActivity.this, "Couldn't load options: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buildChoiceViews(LabStep step, List<LabStepOption> options) {
        choiceContainer.removeAllViews();
        List<LabStepOption> previousSelection = selections.get(currentStepIndex);

        if (STEP_MULTI_SELECT.equals(step.getStep_type())) {
            for (LabStepOption option : options) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(option.getLabel());
                checkBox.setTag(option);
                if (previousSelection != null && containsOption(previousSelection, option)) {
                    checkBox.setChecked(true);
                }
                choiceContainer.addView(checkBox);
            }
            return;
        }

        // single_select and image_select both render as a RadioGroup.
        // Note: RadioGroup only tracks direct-child RadioButtons for its
        // exclusivity logic, so for image_select we add the ImageView and its
        // RadioButton as separate direct children (stacked), rather than
        // wrapping each pair in its own sub-layout — nesting would silently
        // break the single-choice behavior.
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        for (LabStepOption option : options) {
            if (STEP_IMAGE_SELECT.equals(step.getStep_type())) {
                ImageView imageView = new ImageView(this);
                int drawableId = getResources().getIdentifier(option.getValue(), "drawable", getPackageName());
                if (drawableId != 0) {
                    imageView.setImageResource(drawableId);
                }
                imageView.setAdjustViewBounds(true);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(160)));
                radioGroup.addView(imageView);
            }

            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option.getLabel());
            radioButton.setTag(option);
            radioGroup.addView(radioButton);

            if (previousSelection != null && containsOption(previousSelection, option)) {
                radioButton.setChecked(true);
            }
        }

        choiceContainer.addView(radioGroup);
    }

    private boolean containsOption(List<LabStepOption> list, LabStepOption option) {
        for (LabStepOption o : list) {
            if (o.getOption_id().equals(option.getOption_id())) return true;
        }
        return false;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void captureCurrentStepState(int index) {
        if (steps.isEmpty()) return;

        List<LabStepOption> selected = new ArrayList<>();
        for (int i = 0; i < choiceContainer.getChildCount(); i++) {
            View child = choiceContainer.getChildAt(i);
            if (child instanceof RadioGroup) {
                RadioGroup rg = (RadioGroup) child;
                int checkedId = rg.getCheckedRadioButtonId();
                if (checkedId != -1) {
                    RadioButton rb = rg.findViewById(checkedId);
                    LabStepOption option = (LabStepOption) rb.getTag();
                    if (option != null) selected.add(option);
                }
            } else if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                if (cb.isChecked()) {
                    LabStepOption option = (LabStepOption) cb.getTag();
                    if (option != null) selected.add(option);
                }
            }
        }
        selections.put(index, selected);

        String justification = etJustification.getVisibility() == View.VISIBLE
                ? etJustification.getText().toString()
                : null;
        justifications.put(index, justification);
    }

    private void goToPreviousStep() {
        if (currentStepIndex == 0) return;
        captureCurrentStepState(currentStepIndex);
        renderStep(currentStepIndex - 1);
    }

    private void onNextClicked() {
        captureCurrentStepState(currentStepIndex);

        LabStep step = steps.get(currentStepIndex);
        List<LabStepOption> selected = selections.get(currentStepIndex);
        String justification = justifications.get(currentStepIndex);

        if (selected == null || selected.isEmpty()) {
            Toast.makeText(this, "Please make a selection before continuing.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (step.getRequires_justification() && (justification == null || justification.trim().isEmpty())) {
            Toast.makeText(this, "Please explain your reasoning before continuing.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnNext.setEnabled(false);

        // single_select / image_select: one option. multi_select: save one
        // response row per selected option (selected_option_id is a single FK,
        // so multi-select has to fan out into multiple inserts).
        saveResponsesForStep(step, selected, justification, 0);
    }

    private void saveResponsesForStep(LabStep step, List<LabStepOption> selected, String justification, int selectedIdx) {
        if (selectedIdx >= selected.size()) {
            btnNext.setEnabled(true);
            advanceOrFinish();
            return;
        }

        LabStepOption option = selected.get(selectedIdx);
        // Only the last save for this step carries the justification, so it
        // isn't duplicated across multiple multi_select rows.
        String justificationForThisRow = (selectedIdx == selected.size() - 1) ? justification : null;

        LabSimulationRepository.saveResponse(attemptId, step.getStep_id(), option.getOption_id(), justificationForThisRow,
                new LabSimulationRepository.Callback<Unit>() {
                    @Override
                    public void onSuccess(Unit result) {
                        saveResponsesForStep(step, selected, justification, selectedIdx + 1);
                    }

                    @Override
                    public void onError(String message) {
                        btnNext.setEnabled(true);
                        Toast.makeText(LabSimulationActivity.this, "Couldn't save response: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void advanceOrFinish() {
        if (currentStepIndex < steps.size() - 1) {
            renderStep(currentStepIndex + 1);
        } else {
            finishLab();
        }
    }

    private void finishLab() {
        String architectureValue = firstSelectedValue(0);
        String databaseValue = firstSelectedValue(2);
        String communicationValue = firstSelectedValue(3);
        String diagramValue = firstSelectedValue(4);

        if (architectureValue == null || databaseValue == null || communicationValue == null || diagramValue == null) {
            Toast.makeText(this, "Missing a required selection — please review your answers.", Toast.LENGTH_LONG).show();
            return;
        }

        btnNext.setEnabled(false);

        LabSimulationRepository.finalizeAttempt(attemptId, labId, architectureValue, databaseValue, communicationValue, diagramValue,
                new LabSimulationRepository.Callback<NewLabResult>() {
                    @Override
                    public void onSuccess(NewLabResult result) {
                        Intent intent = new Intent(LabSimulationActivity.this, LabResultsActivity.class);
                        intent.putExtra("scoreBand", result.getScore_band());
                        intent.putExtra("feedback", result.getFeedback());
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onError(String message) {
                        btnNext.setEnabled(true);
                        Toast.makeText(LabSimulationActivity.this, "Couldn't finalize attempt: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String firstSelectedValue(int stepIndex) {
        List<LabStepOption> selected = selections.get(stepIndex);
        if (selected == null || selected.isEmpty()) return null;
        return selected.get(0).getValue();
    }
}