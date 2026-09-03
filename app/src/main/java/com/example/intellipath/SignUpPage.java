package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
        String passwordVal      = password.getText().toString();

        if (firstNameVal.isEmpty() || lastNameVal.isEmpty() ||emailVal.isEmpty() || passwordVal.isEmpty() || studentNumberVal.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
//TODO: Add validation for password !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        SupabaseAuthRepository.signUp(firstNameVal, lastNameVal, studentNumberVal, emailVal, passwordVal,
                new SupabaseAuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        startActivity(new Intent(SignUpPage.this, RegistrationTwo.class));
                        finish();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(SignUpPage.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}

/// Make a popup window for the terms and conditions ! and that if button not checked toast appears and user must check it.