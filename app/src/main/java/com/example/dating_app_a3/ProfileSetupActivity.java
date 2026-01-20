package com.example.dating_app_a3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dating_app_a3.Models.User;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText nameEditText, birthdayEditText, biographyEditText;
    private RadioGroup genderRadioGroup, preferredSexRadioGroup;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.editTextName);
        birthdayEditText = findViewById(R.id.editTextBirthday);
        biographyEditText = findViewById(R.id.editTextBiography);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        preferredSexRadioGroup = findViewById(R.id.radioGroupPreferredSex);
        saveButton = findViewById(R.id.buttonSaveProfile);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();
            }
        });

        getFCMToken();
    }

    private void saveUserProfile() {
        String name = nameEditText.getText().toString().trim();
        String birthday = birthdayEditText.getText().toString().trim();
        String biography = biographyEditText.getText().toString().trim();

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        int selectedPreferredSexId = preferredSexRadioGroup.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(birthday) || TextUtils.isEmpty(biography)
                || selectedGenderId == -1 || selectedPreferredSexId == -1) {
            Toast.makeText(ProfileSetupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender.getText().toString();

        RadioButton selectedPreferredSex = findViewById(selectedPreferredSexId);
        String preferredSex = selectedPreferredSex.getText().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            User user= new User(currentUser.getEmail(), "user");
            user.setName(name);
            user.setBirthday(birthday);
            user.setBiography(biography);
            user.setGender(gender);
            user.setPreferredSex(preferredSex);
            Map<String, Object> userMap = user.toMap();
            db.collection("users").document(userId).set(userMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileSetupActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileSetupActivity.this, SetProfilePictureActivity.class));
                            finish();
                        } else {
                            Toast.makeText(ProfileSetupActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });;

//            db.collection("users").document(userId)
//                    .update("name", name, "birthday", birthday, "biography", biography,
//                            "gender", gender, "preferredSex", preferredSex)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(ProfileSetupActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(ProfileSetupActivity.this, SetProfilePictureActivity.class));
//                            finish();
//                        } else {
//                            Toast.makeText(ProfileSetupActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
//                        }
//                    });
        }
    }


    public void showDatePicker(View view) {
        TextInputEditText editTextBirthday = findViewById(R.id.editTextBirthday);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Birthday")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Format the selected date
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = sdf.format(selection);

            editTextBirthday.setText(formattedDate);
        });

        datePicker.show(getSupportFragmentManager(), datePicker.toString());
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String token = task.getResult();

            }
        });
    }
}
