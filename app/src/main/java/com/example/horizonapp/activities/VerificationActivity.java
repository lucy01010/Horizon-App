package com.example.horizonapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.horizonapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public final class VerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        TextView verificationMessage = findViewById(R.id.verification_message);
        verificationMessage.setText(getString(R.string.verification_message));

        checkEmailVerification();
    }

    private void checkEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            // Email is verified, navigate to Welcome Activity
            startActivity(new Intent(VerificationActivity.this, WelcomeActivity.class));
            finish(); // Close the current activity to prevent the user from going back
        }
    }
}
