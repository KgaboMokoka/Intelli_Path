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

public class ResetPasswordActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";

    private String userEmail;
    private EditText resetCode, newPassword, confirmNewPassword;
    private Button btnUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        userEmail = getIntent().getStringExtra(EXTRA_EMAIL);

        resetCode = findViewById(R.id.resetCode);
        newPassword = findViewById(R.id.newPassword);
        confirmNewPassword = findViewById(R.id.confirmNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        TextView info = findViewById(R.id.resetInfo);
        if (userEmail != null) {
            info.setText("If " + userEmail + " is registered, we sent a code to it. "
                    + "Enter the code and choose a new password.");
        }

        btnUpdatePassword.setOnClickListener(v -> submit());
        findViewById(R.id.btnResendCode).setOnClickListener(v -> resendCode());
        findViewById(R.id.btnBackToLogin).setOnClickListener(v -> finish());
    }

    private void submit() {

        if (userEmail == null) {
            Toast.makeText(this, "Missing email. Please start again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String code = resetCode.getText().toString().trim();
        String passwordVal = newPassword.getText().toString();
        String confirmVal = confirmNewPassword.getText().toString();

        if (code.length() < 6) {
            resetCode.setError("Enter the 6-digit code from your email");
            resetCode.requestFocus();
            return;
        }

        String error = PasswordValidator.getError(passwordVal);

        if (error != null) {
            newPassword.setError(error);
            newPassword.requestFocus();
            return;
        }

        if (!passwordVal.equals(confirmVal)) {
            confirmNewPassword.setError("Passwords do not match");
            confirmNewPassword.requestFocus();
            return;
        }

        btnUpdatePassword.setEnabled(false);

        SupabaseAuthRepository.resetPasswordWithCode(
                userEmail,
                code,
                passwordVal,
                new SupabaseAuthRepository.AuthCallback() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                ResetPasswordActivity.this,
                                "Password updated. Please log in with your new password.",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, LoginPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        btnUpdatePassword.setEnabled(true);
                        Toast.makeText(
                                ResetPasswordActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void resendCode() {

        if (userEmail == null) return;

        SupabaseAuthRepository.sendPasswordReset(
                userEmail,
                new SupabaseAuthRepository.AuthCallback() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(
                                ResetPasswordActivity.this,
                                "A new code has been sent.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(
                                ResetPasswordActivity.this,
                                message.contains("rate_limit")
                                        ? "Too many requests. Please wait a few minutes and try again."
                                        : message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}