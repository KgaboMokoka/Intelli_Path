package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intellipath.data.SupabaseAuthRepository;

import org.jetbrains.annotations.NotNull;

public class ConfirmEmailPage extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";

    private String userEmail;
    private EditText confirmCode;
    private Button verifyCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email_page);

        userEmail = getIntent().getStringExtra(EXTRA_EMAIL);

        confirmCode = findViewById(R.id.confirmCode);
        verifyCodeButton = findViewById(R.id.verifyCodeButton);

        TextView message = findViewById(R.id.confirmEmailMessage);
        if (userEmail != null) {
            message.setText("We sent a verification code to " + userEmail
                    + ". Enter it below to verify your email.");
        }

        verifyCodeButton.setOnClickListener(v -> verifyCode());

        findViewById(R.id.openEmailAppButton).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.resendEmailButton).setOnClickListener(v -> resendConfirmation());

        findViewById(R.id.backToLoginLink).setOnClickListener(v -> {
            startActivity(new Intent(ConfirmEmailPage.this, LoginPage.class));
            finish();
        });
    }

    private void verifyCode() {

        if (userEmail == null) {
            Toast.makeText(this, "Missing email. Please register again.", Toast.LENGTH_LONG).show();
            return;
        }

        String code = confirmCode.getText().toString().trim();

        if (code.length() < 6) {
            confirmCode.setError("Enter the 6-digit code from your email");
            confirmCode.requestFocus();
            return;
        }

        verifyCodeButton.setEnabled(false);

        SupabaseAuthRepository.verifySignupCode(
                userEmail,
                code,
                new SupabaseAuthRepository.LoginCallback() {

                    @Override
                    public void onNeedsRegistration() {
                        Intent intent = new Intent(ConfirmEmailPage.this, RegistrationTwo.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onComplete() {
                        Intent intent = new Intent(ConfirmEmailPage.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        verifyCodeButton.setEnabled(true);
                        Toast.makeText(ConfirmEmailPage.this, message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void resendConfirmation() {
        if (userEmail == null) return;

        SupabaseAuthRepository.resendConfirmation(userEmail,
                new SupabaseAuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ConfirmEmailPage.this, "A new code has been sent.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(ConfirmEmailPage.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}