package com.example.intellipath;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TermsAndConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        findViewById(R.id.btnBackFromTerms).setOnClickListener(v -> finish());
    }
}