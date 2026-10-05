package com.example.intellipath;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intellipath.data.SupabaseAuthRepository;

import org.jetbrains.annotations.NotNull;

public class SignUpPage extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private EditText firstName, lastName, studentNumber, email, password, confirmPassword;
    private CheckBox termsCheckBox;
    private TextView termsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        studentNumber = findViewById(R.id.studentNumber);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        termsCheckBox = findViewById(R.id.termsAndConditions);
        termsText = findViewById(R.id.termsText);

        setupTermsText();

        findViewById(R.id.registerButton).setOnClickListener(v -> signUp());
        findViewById(R.id.loginPage).setOnClickListener(v ->
                startActivity(new Intent(SignUpPage.this, LoginPage.class)));
    }

    // ============================================================
    // TERMS AND CONDITIONS TEXT (with tappable link)
    // ============================================================

    private void setupTermsText() {

        String prefix = "I agree to the ";
        String link = "Terms and Conditions";
        String suffix = " *";

        SpannableString text = new SpannableString(prefix + link + suffix);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(
                        SignUpPage.this,
                        TermsAndConditionsActivity.class
                ));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(R.color.primary, getTheme()));
            }
        };

        text.setSpan(
                clickableSpan,
                prefix.length(),
                prefix.length() + link.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        termsText.setText(text);
        termsText.setMovementMethod(LinkMovementMethod.getInstance());
        termsText.setHighlightColor(Color.TRANSPARENT);
    }

    // ============================================================
    // PASSWORD VALIDATION
    // Returns an error message, or null if the password is valid.
    // ============================================================

    private String getPasswordError(String value) {

        if (value.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.";
        }
        if (!value.matches(".*[A-Z].*")) {
            return "Password must contain an uppercase letter.";
        }
        if (!value.matches(".*[a-z].*")) {
            return "Password must contain a lowercase letter.";
        }
        if (!value.matches(".*[0-9].*")) {
            return "Password must contain a number.";
        }
        if (!value.matches(".*[^A-Za-z0-9].*")) {
            return "Password must contain a special character.";
        }
        if (value.contains(" ")) {
            return "Password must not contain spaces.";
        }
        return null;
    }

    // ============================================================
    // SIGN UP
    // ============================================================

    private void signUp() {

        String firstNameVal       = firstName.getText().toString().trim();
        String lastNameVal        = lastName.getText().toString().trim();
        String studentNumberVal   = studentNumber.getText().toString().trim();
        String emailVal           = email.getText().toString().trim();
        String passwordVal        = password.getText().toString();
        String confirmPasswordVal = confirmPassword.getText().toString();

        // Student number is optional: send null instead of an empty string
        // (the column is UNIQUE, so "" would collide between students).
        String studentNumberOrNull = studentNumberVal.isEmpty() ? null : studentNumberVal;

        // --- Required fields ---
        if (firstNameVal.isEmpty()) {
            firstName.setError("First name is required");
            firstName.requestFocus();
            return;
        }

        if (lastNameVal.isEmpty()) {
            lastName.setError("Surname is required");
            lastName.requestFocus();
            return;
        }

        if (emailVal.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
            email.setError("Enter a valid email address");
            email.requestFocus();
            return;
        }

        // --- Password ---
        String passwordError = PasswordValidator.getError(passwordVal);

        if (passwordError != null) {
            password.setError(passwordError);
            password.requestFocus();
            return;
        }

        if (!passwordVal.equals(confirmPasswordVal)) {
            confirmPassword.setError("Passwords do not match");
            confirmPassword.requestFocus();
            return;
        }

        // --- Terms ---
        if (!termsCheckBox.isChecked()) {
            Toast.makeText(
                    this,
                    "Please agree to the Terms and Conditions to continue.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        SupabaseAuthRepository.signUp(
                firstNameVal,
                lastNameVal,
                studentNumberOrNull,
                emailVal,
                passwordVal,
                new SupabaseAuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(SignUpPage.this, ConfirmEmailPage.class);
                        intent.putExtra(ConfirmEmailPage.EXTRA_EMAIL, emailVal);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(SignUpPage.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}