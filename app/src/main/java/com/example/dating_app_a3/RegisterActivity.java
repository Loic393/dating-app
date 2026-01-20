package com.example.dating_app_a3;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dating_app_a3.Models.User;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailRegisterEditText);
        passwordEditText = findViewById(R.id.passwordRegisterEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        FirebaseUser user = mAuth.getCurrentUser();
//        if (user != null){
//            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password, confirmPassword;
                email = String.valueOf(emailEditText.getText());
                password = String.valueOf(passwordEditText.getText());
                confirmPassword = String.valueOf(confirmPasswordEditText.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Please confirm your password",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!TextUtils.equals(password,confirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Password and password confirmation do not match",Toast.LENGTH_SHORT).show();
                    passwordEditText.setText("");
                    confirmPasswordEditText.setText("");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(RegisterActivity.this, "Successfully registered!", Toast.LENGTH_LONG).show();
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    String userId = user.getUid();
//                                    User userObj = new User(email, "user");
//                                    Map<String, Object> userMap = userObj.toMap();
//                                    db.collection("users").document(userId).set(userMap);
                                    Intent intent = new Intent(RegisterActivity.this, ProfileSetupActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        passwordEditText.setError("Your password is too weak!");
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        emailEditText.setError("Email is invalid!");
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        emailEditText.setError("A user is already registered with this email");
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            }
        });

    }

    public void goBack(View view) {
        finish();
    }
}