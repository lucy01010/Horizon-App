package com.example.horizonapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.horizonapp.MainActivity;
import com.example.horizonapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class RegistrationActivity extends AppCompatActivity {
    private EditText mName, mEmail, mPassword, mConfirmPassword;
    private Button mRegisterbtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mRegisterbtn = findViewById(R.id.register_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must be at least 6 characters");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    mConfirmPassword.setError("Passwords do not match");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String displayName = mName.getText().toString().trim();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();

                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> profileTask) {
                                        if (profileTask.isSuccessful()) {
                                            checkAndSaveUserDataToFirestore(user);
                                            sendEmailVerification();
                                        } else {
                                            Toast.makeText(RegistrationActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegistrationActivity.this, "User is not signed in", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    private void checkAndSaveUserDataToFirestore(FirebaseUser user) {
        String userId = user.getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // User document already exists, do nothing
                    Log.d("Firestore", "User document already exists");
                } else {
                    // User document does not exist, create it
                    String email = user.getEmail();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("saved", new ArrayList<String>());

                    db.collection("users").document(userId).set(userData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(RegistrationActivity.this, "User data saved", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(RegistrationActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show());
                }
            } else {
                Log.e("Firestore", "Error checking user document", task.getException());
            }
        });
    }

    private void sendEmailVerification() {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationActivity.this, VerificationActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void login(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }
}
