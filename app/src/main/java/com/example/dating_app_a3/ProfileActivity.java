package com.example.dating_app_a3;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dating_app_a3.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView imageViewProfile;
    TextView nameTextView;
    TextView birthdayTextView;

    FirebaseAuth auth;
    private TextView textViewHeight;
    private EditText editTextHeight;
    private Button btnEditHeight;

    private TextView textViewBio;
    private EditText editTextBio;
    private Button btnEditBio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageViewProfile = findViewById(R.id.imageViewProfile);
        nameTextView = findViewById(R.id.textViewName);
        birthdayTextView = findViewById(R.id.textViewBirthday);


        textViewHeight = findViewById(R.id.textViewHeight);
        editTextHeight = findViewById(R.id.editTextHeight);
        btnEditHeight = findViewById(R.id.btnEditHeight);

        textViewBio = findViewById(R.id.textViewBio);
        editTextBio = findViewById(R.id.editTextBio);
        btnEditBio = findViewById(R.id.btnEditBio);

        textViewHeight.setVisibility(View.VISIBLE);
        editTextHeight.setVisibility(View.GONE);

        textViewBio.setVisibility(View.VISIBLE);
        editTextBio.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Retrieve the user document from the "users" collection
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Retrieve profile picture URLs as List<String>
                            List<String> profilePictureUrls = (List<String>) documentSnapshot.get("profilePictureUrls");

                            // Use the first URL (if available) or set a default placeholder
                            String imageUrl = (profilePictureUrls != null && !profilePictureUrls.isEmpty())
                                    ? profilePictureUrls.get(0)
                                    : null;

                            Glide.with(this)
                                    .load(imageUrl)
                                    .apply(new RequestOptions().placeholder(R.drawable.profilepic))
                                    .into(imageViewProfile);

                            nameTextView.setText(documentSnapshot.getString("name"));
                            String height = documentSnapshot.get("height") != null ? documentSnapshot.get("height").toString() : "N/A";
                            textViewHeight.setText(height);
                            birthdayTextView.setText(documentSnapshot.getString("birthday"));
                            textViewBio.setText(documentSnapshot.getString("biography"));
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to fetch the user document
                        Log.e(TAG, "Error fetching user document: " + e.getMessage());
                    });
        }


        btnEditHeight.setOnClickListener(v -> toggleEditMode(textViewHeight, editTextHeight, btnEditHeight));

        btnEditBio.setOnClickListener(v -> toggleEditMode(textViewBio, editTextBio, btnEditBio));
    }

    public void changeAvatar(View view){
        Intent intent = new Intent(ProfileActivity.this, SetProfilePictureActivity.class);
        startActivity(intent);
    }

    private void toggleEditMode(TextView textView, EditText editText, Button editBtn) {
        if (textView.getVisibility() == View.VISIBLE) {
            // Switch to edit mode
            textView.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            editText.setText(textView.getText());
            editBtn.setText("Save");// Set initial text
        } else {
            // Switch to view mode
            textView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);



            // Save the edited information to the database based on the view being edited
            if (textView == textViewHeight) {
                // Handle saving height logic
                saveHeightToDatabase(editText.getText().toString());
            } else if (textView == textViewBio) {
                // Handle saving biography logic
                saveBiographyToDatabase(editText.getText().toString());
            }
        }
    }


    private void saveHeightToDatabase(String height) {
        if (!height.isEmpty()) {
            try {
                int heightValue = Integer.parseInt(height);

                if (heightValue >= 50 && heightValue <= 215) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser currentUser = auth.getCurrentUser();

                    if (currentUser != null) {
                        String userId = currentUser.getUid();

                        // Update the "users" collection with the new height value
                        db.collection("users").document(userId)
                                .update("height", heightValue)
                                .addOnSuccessListener(aVoid -> {Toast.makeText(ProfileActivity.this, "Height updated!", Toast.LENGTH_SHORT).show(); textViewHeight.setText(height.toString());})
                                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Error updating height!", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // Display an error message for an invalid height range
                    Toast.makeText(ProfileActivity.this, "Please enter a valid height from 50 to 215cm!", Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException e) {
                // Display an error message for a non-integer input
                // For example, show a Toast message or set an error state on the EditText
                Log.e(TAG, "Invalid height format: " + height, e);
                Toast.makeText(ProfileActivity.this, "Invalid format!", Toast.LENGTH_LONG).show();
            }
        } else {
            // Handle the case of an empty height input
            // For example, show a Toast message or set an error state on the EditText
            Toast.makeText(ProfileActivity.this, "Please enter a valid height from 50 to 215cm!", Toast.LENGTH_LONG).show();
        }
    }

    private void saveBiographyToDatabase(String biography) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Update the "users" collection with the new biography value
            db.collection("users").document(userId)
                    .update("biography", biography)
                    .addOnSuccessListener(aVoid -> {Toast.makeText(ProfileActivity.this, "Biography updated!", Toast.LENGTH_SHORT).show(); textViewBio.setText(biography);})
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Error updating biography!", Toast.LENGTH_SHORT).show());
        }
    }


    public void goBack(View v){
        finish();
    }


}