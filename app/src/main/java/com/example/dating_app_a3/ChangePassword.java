package com.example.dating_app_a3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassword extends AppCompatActivity {

    EditText emailEditText, newPasswordEditText, confirmPasswordEditText, oldPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        oldPasswordEditText = findViewById(R.id.oldPasswordEditText);
    }

    public void changePassword(View v) {
        String email, newPassword, confirmPassword, oldPassword;
        email = String.valueOf(emailEditText.getText());
        newPassword = String.valueOf(newPasswordEditText.getText());
        confirmPassword = String.valueOf(confirmPasswordEditText.getText());
        oldPassword = String.valueOf(oldPasswordEditText.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ChangePassword.this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(ChangePassword.this, "Please enter your current password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(ChangePassword.this, "Please enter the new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(ChangePassword.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.equals(newPassword, confirmPassword)) {
            Toast.makeText(ChangePassword.this, "Password and password confirmation do not match", Toast.LENGTH_SHORT).show();
            newPasswordEditText.setText("");
            confirmPasswordEditText.setText("");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, oldPassword);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePassword.this, "Password updated!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ChangePassword.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChangePassword.this, "Error auth failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}