package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.intellipath.data.SupabaseAuthRepository;

import org.jetbrains.annotations.NotNull;

public class SignUpPage extends AppCompatActivity {

    private EditText firstName, lastName, studentNumber, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        studentNumber = findViewById(R.id.studentNumber);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        findViewById(R.id.registerButton).setOnClickListener(v -> signUp());
    }

    private void signUp() {
        String firstNameVal     = firstName.getText().toString().trim();
        String lastNameVal      = lastName.getText().toString().trim();
        String studentNumberVal = studentNumber.getText().toString().trim();
        String emailVal         = email.getText().toString().trim();
        String passwordVal      = password.getText().toString().trim();

        if (emailVal.isEmpty() || passwordVal.isEmpty() || studentNumberVal.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO: Add validation for password !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        SupabaseAuthRepository.signUp(firstNameVal, lastNameVal, studentNumberVal, emailVal, passwordVal,
                new SupabaseAuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(SignUpPage.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(SignUpPage.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}