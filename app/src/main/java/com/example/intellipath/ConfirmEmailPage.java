package com.example.intellipath;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intellipath.data.SupabaseAuthRepository;
import org.jetbrains.annotations.NotNull;

public class ConfirmEmailPage extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email_page);

        userEmail = getIntent().getStringExtra(EXTRA_EMAIL);

        TextView message = findViewById(R.id.confirmEmailMessage);
        if (userEmail != null) {
            message.setText("You have entered " + userEmail + " for your account. Please verify this email by checking your emails.");
        }

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

    private void resendConfirmation() {
        if (userEmail == null) return;

        SupabaseAuthRepository.resendConfirmation(userEmail,
                new SupabaseAuthRepository.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ConfirmEmailPage.this, "Confirmation email resent.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(ConfirmEmailPage.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}