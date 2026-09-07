package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intellipath.data.SupabaseAuthRepository;
import org.jetbrains.annotations.NotNull;

public class LoginPage extends AppCompatActivity {

    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        findViewById(R.id.loginButton).setOnClickListener(v -> logIn());
        findViewById(R.id.registerPage).setOnClickListener(v ->
                startActivity(new Intent(LoginPage.this, SignUpPage.class)));
    }

    private void logIn() {
        String emailVal = email.getText().toString().trim();
        String passwordVal = password.getText().toString();

        if (emailVal.isEmpty() || passwordVal.isEmpty()) {
            Toast.makeText(this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseAuthRepository.signIn(emailVal, passwordVal,
                new SupabaseAuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(LoginPage.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(LoginPage.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}