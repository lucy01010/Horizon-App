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

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView emailTv;
    TextView passwordTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailTv = findViewById(R.id.email);
        passwordTv = findViewById(R.id.password);
        Button button = findViewById(R.id.register_button);

        button.setOnClickListener(v->{
            boolean isValid = true;
            String password = passwordTv.getText().toString();
            String email = emailTv.getText().toString();
            if (password.isEmpty()) {
                passwordTv.setError("Password cannot be empty");
                isValid = false;
            }

            if (email.isEmpty()) {
                emailTv.setError("Email cannot be empty");
                isValid= false;
            }

            if (!isValid)
                return;

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e("Login: ", "Sign In With Email Failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                passwordTv.setError("email_or_password is incorrect");
                                emailTv.setError("email_or_password_is_incorrect");
                            } else {

                            }
                        }
                    });

//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        });
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}
