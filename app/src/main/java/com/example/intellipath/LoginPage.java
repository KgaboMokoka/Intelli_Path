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
}
