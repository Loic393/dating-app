package com.example.dating_app_a3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SetProfilePictureActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private Button chooseImageButton, saveButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_picture);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImageView = findViewById(R.id.profileImageView);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        saveButton = findViewById(R.id.saveButton);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfilePicture();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveProfilePicture() {
        if (selectedImageUri != null) {
            // Create a unique name for the image in Firebase Storage
            String imageName = "profile_picture_" + System.currentTimeMillis() + ".jpg";

            // Reference to the Firebase Storage location
            StorageReference storageRef = storage.getReference().child("profile_pictures").child(imageName);

            // Upload the image to Firebase Storage
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the user's profilePictureUrls in Firestore
                            updateProfilePictureUrls(uri.toString());

                            // Display the selected image on the ImageView
                            profileImageView.setImageURI(selectedImageUri);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(SetProfilePictureActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SetProfilePictureActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfilePictureUrls(String downloadUrl) {
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Retrieve the existing profilePictureUrls
                List<String> profilePictureUrls = (List<String>) task.getResult().get("profilePictureUrls");

                if (profilePictureUrls == null) {
                    profilePictureUrls = new ArrayList<>();
                }

                // Add the new download URL to the list
                profilePictureUrls.add(downloadUrl);

                // Update the user document with the new profilePictureUrls
                userRef.update("profilePictureUrls", profilePictureUrls)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SetProfilePictureActivity.this, "Profile picture saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SetProfilePictureActivity.this, SwipeActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SetProfilePictureActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Display the selected image on the ImageView
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    public void goBack(View v){
        finish();
    }
}
