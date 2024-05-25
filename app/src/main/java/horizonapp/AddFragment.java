package com.example.horizonapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.horizonapp.activities.AddMarkerActivity;
import com.example.horizonapp.models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddFragment extends Fragment {
    private static final String TAG = "YourFragment";
    private static final int PICK_IMAGE_REQUEST = 1;
    private TextView nameTv;
    private TextView descTv;
    private Button categoryTv;
    private TextView ratingTv;
    private ImageButton addBtn;
    private ImageView productPhotoImageView;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private static final int REQUEST_CODE_LOCATION = 100; // Request code for location

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        nameTv = view.findViewById(R.id.product_name);
        descTv = view.findViewById(R.id.product_details);
        ratingTv = view.findViewById(R.id.product_rating);
        categoryTv = view.findViewById(R.id.product_category);
        addBtn = view.findViewById(R.id.btn_done);
        addBtn.setOnClickListener(v -> {
            saveDataToFirestore();
        });

        productPhotoImageView = view.findViewById(R.id.product_photo);
        productPhotoImageView.setOnClickListener(v -> {
            // Start intent to pick an image from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        categoryTv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMarkerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOCATION);
        });
    }

    private void saveDataToFirestore() {
        // Check if an image is selected
        if (productPhotoImageView.getDrawable() == null) {
            Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the image to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ((BitmapDrawable) productPhotoImageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Generate a unique name for the image
        String imageName = UUID.randomUUID().toString() + ".jpg";

        // Define the path where the image will be stored in Firebase Storage
        String imagePath = "images/" + imageName;

        // Create a storage reference with the image path
        StorageReference imageRef = storageRef.child(imagePath);

        // Upload the image to Firebase Storage
        imageRef.putBytes(imageData).addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // URL of the uploaded image
                String imageUrl = uri.toString();
                Log.d(TAG, "Image uploaded. URL: " + imageUrl);

                // Show a toast message indicating successful upload
                Toast.makeText(getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                // Save the data to Firestore with the image URL
                saveDataToFirestore(imageUrl);
            });
        }).addOnFailureListener(e -> {
            // Error occurred while uploading the image
            Log.e(TAG, "Error uploading image", e);
            Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveDataToFirestore(String imageUrl) {
        PostModel product = new PostModel(nameTv.getText().toString(), categoryTv.getText().toString(), descTv.getText().toString(), imageUrl, Double.parseDouble(ratingTv.getText().toString()));

        db.collection("products").add(product).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                Toast.makeText(getActivity(), "Product added successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(getActivity(), "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOCATION && resultCode == Activity.RESULT_OK && data != null) {
            String addressDetails = data.getStringExtra("LOCATION_ADDRESS");
            if (addressDetails != null) {
                categoryTv.setText(addressDetails);  // Set the TextView with the address details
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);

                productPhotoImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
