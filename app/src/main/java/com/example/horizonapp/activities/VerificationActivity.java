package com.example.horizonapp.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.horizonapp.R;

public class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        TextView verificationMessage = findViewById(R.id.verification_message);
        verificationMessage.setText(getString(R.string.verification_message));
    }
}
