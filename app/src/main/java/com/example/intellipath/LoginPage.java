package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intellipath.MainActivity;
import com.example.intellipath.SignUpPage;
import com.example.intellipath.data.SupabaseAuthRepository;

import org.jetbrains.annotations.NotNull;

import android.text.InputType;
import android.util.Patterns;
import android.widget.FrameLayout;
import androidx.appcompat.app.AlertDialog;

public class LoginPage extends AppCompatActivity {

    private EditText email, password;

    // Admin MVP: hard-coded admin login credentials
    // TODO: Change these test credentials later.
    private static final String ADMIN_EMAIL = "admin@intellipath.co.za";
    private static final String ADMIN_PASSWORD = "Admin123!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        findViewById(R.id.loginButton).setOnClickListener(v -> logIn());

        findViewById(R.id.registerPage).setOnClickListener(v ->
                startActivity(
                        new Intent(
                                LoginPage.this,
                                SignUpPage.class
                        )
                )
        );

        findViewById(R.id.forgotPassword).setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void logIn() {

        String emailVal = email.getText().toString().trim();
        String passwordVal = password.getText().toString();

        // Make sure both fields have been filled in
        if (emailVal.isEmpty() || passwordVal.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter your email and password.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ---------------------------------------------------------
        // ADMIN LOGIN
        // ---------------------------------------------------------
        // Check the admin credentials BEFORE attempting Supabase login.
        if (emailVal.equals(ADMIN_EMAIL)
                && passwordVal.equals(ADMIN_PASSWORD)) {

            Intent intent = new Intent(
                    LoginPage.this,
                    AdminDash.class
            );

            startActivity(intent);
            finish();

            // Do NOT continue to the normal student login.
            return;
        }

        // ---------------------------------------------------------
        // STUDENT LOGIN
        // ---------------------------------------------------------
        // If the credentials are not the admin credentials,
        // continue with the existing Supabase authentication flow.
        SupabaseAuthRepository.signIn(
                emailVal,
                passwordVal,
                new SupabaseAuthRepository.LoginCallback() {

                    @Override
                    public void onNeedsRegistration() {

                        startActivity(
                                new Intent(
                                        LoginPage.this,
                                        RegistrationTwo.class
                                )
                        );

                        finish();
                    }

                    @Override
                    public void onComplete() {

                        startActivity(
                                new Intent(
                                        LoginPage.this,
                                        MainActivity.class
                                )
                        );

                        finish();
                    }

                    @Override
                    public void onError(@NotNull String message) {

                        Toast.makeText(
                                LoginPage.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void showForgotPasswordDialog() {

        final EditText input = new EditText(this);
        input.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        input.setHint("Email");
        input.setText(email.getText().toString().trim());

        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        FrameLayout container = new FrameLayout(this);
        container.setPadding(pad, pad / 2, pad, 0);
        container.addView(input);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reset your password")
                .setMessage("Enter your email and we'll send you a code to reset it.")
                .setView(container)
                .setPositiveButton("Send code", null)   // set below so it doesn't auto-close
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {

            final android.widget.Button sendButton =
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            sendButton.setOnClickListener(v -> {

                final String emailVal = input.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
                    input.setError("Enter a valid email address");
                    return; // dialog stays open
                }

                sendButton.setEnabled(false);

                SupabaseAuthRepository.sendPasswordReset(
                        emailVal,
                        new SupabaseAuthRepository.AuthCallback() {

                            @Override
                            public void onSuccess() {
                                dialog.dismiss();

                                Toast.makeText(
                                        LoginPage.this,
                                        "If that email is registered, a code is on its way.",
                                        Toast.LENGTH_LONG
                                ).show();

                                Intent intent = new Intent(LoginPage.this, ResetPasswordActivity.class);
                                intent.putExtra(ResetPasswordActivity.EXTRA_EMAIL, emailVal);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(@NotNull String message) {
                                sendButton.setEnabled(true);

                                String friendly = message.contains("rate_limit")
                                        ? "Too many requests. Please wait a few minutes and try again."
                                        : message;

                                Toast.makeText(LoginPage.this, friendly, Toast.LENGTH_LONG).show();
                            }
                        }
                );
            });
        });

        dialog.show();
    }
}
