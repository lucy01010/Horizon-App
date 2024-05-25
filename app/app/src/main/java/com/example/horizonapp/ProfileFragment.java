package com.example.horizonapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.horizonapp.activities.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class  ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView userEmailTextView;
    private EditText userNameTextView;
    private Button logoutButton;
    private ImageView updateUsernameBtn, sendResetEmail;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        userNameTextView = view.findViewById(R.id.username_texttext);
        userEmailTextView = view.findViewById(R.id.email_texttext);
        logoutButton = view.findViewById(R.id.logoutButton);
        updateUsernameBtn = view.findViewById(R.id.updateUsernameBtn);
        sendResetEmail = view.findViewById(R.id.sendResetEmail);
        progressBar = view.findViewById(R.id.progressBar2);

        updateUsernameBtn.setOnClickListener(v -> updateUsername());
        sendResetEmail.setOnClickListener(v -> sendPasswordResetEmail());

        if (mAuth.getCurrentUser() != null) {
            String userName = mAuth.getCurrentUser().getDisplayName();
            String userEmail = mAuth.getCurrentUser().getEmail();
            userNameTextView.setText(userName);
            userEmailTextView.setText(userEmail);
        }

        userNameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentUser = mAuth.getCurrentUser();
                if (!Objects.equals(currentUser.getDisplayName(), s.toString())) {
                    updateUsernameBtn.setVisibility(View.VISIBLE);
                } else {
                    updateUsernameBtn.setVisibility(View.GONE);
                }
            }
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            // Navigate to the WelcomeActivity
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Optional flags to clear back stack
            startActivity(intent);
        });

        return view;
    }

    private void updateUsername() {
        String displayName = userNameTextView.getText().toString();

        if (currentUser != null) {
            if (Objects.equals(currentUser.getDisplayName(), displayName)) {
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();

        if (currentUser != null) {
            currentUser.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                if (profileTask.isSuccessful()) {
                    Log.d("ProfileUpdate", "Profile updated successfully");
                    progressBar.setVisibility(View.GONE);
                    updateUsernameBtn.setVisibility(View.GONE);
                } else {
                    Log.e("ProfileUpdate", "Failed to update profile", profileTask.getException());
                    Toast.makeText(requireActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Log.w("ProfileUpdate", "Current user is null");
        }
    }

    private void sendPasswordResetEmail() {
        if (currentUser == null) {
            return;
        }

        mAuth.sendPasswordResetEmail(Objects.requireNonNull(currentUser.getEmail()))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), R.string.password_reset_email_sent_successfully, Toast.LENGTH_SHORT).show();
                        Log.d("Edit Account: ", "Password Reset Email Sent Successfully");
                    } else {
                        Log.e("Edit Account: ", "Error Sending Password Reset Email", task.getException());
                    }
                });
    }
}