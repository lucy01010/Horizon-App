package com.example.horizonapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.horizonapp.MainActivity;
import com.example.horizonapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView emailTv;
    private TextView passwordTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        emailTv = findViewById(R.id.email);
        passwordTv = findViewById(R.id.password);
        Button button = findViewById(R.id.register_button);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            checkAndSaveUserDataToFirestore(currentUser);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        button.setOnClickListener(v -> {
            boolean isValid = true;
            String password = passwordTv.getText().toString();
            String email = emailTv.getText().toString();
            if (password.isEmpty()) {
                passwordTv.setError("Password cannot be empty");
                isValid = false;
            }

            if (email.isEmpty()) {
                emailTv.setError("Email cannot be empty");
                isValid = false;
            }

            if (!isValid)
                return;

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                checkAndSaveUserDataToFirestore(user);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else if (user != null) {
                                // Notify the user to verify their email
                                Log.e("Login: ", "Email not verified");
                            }
                        } else {
                            Log.e("Login: ", "Sign In With Email Failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                passwordTv.setError("email or password is incorrect");
                                emailTv.setError("email or password is incorrect");
                            }
                        }
                    });
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
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data saved"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving user data", e));
                }
            } else {
                Log.e("Firestore", "Error checking user document", task.getException());
            }
        });
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}
