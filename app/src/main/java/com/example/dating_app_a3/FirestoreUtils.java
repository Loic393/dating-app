package com.example.dating_app_a3;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import com.example.dating_app_a3.Models.User;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirestoreUtils {
    public static void getUsersFromFirestore(final FirestoreCallback<List<User>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            User user = User.toObject(document.getData(), document.getId());
                            userList.add(user);
                        }
                        callback.onCallback(userList);
                    } else {
                        Log.e(TAG, "Error getting users", task.getException());
                        callback.onCallback(null);
                    }
                });
    }

    public static void getMatchesFromFirestore(String userId, final FirestoreCallback<List<User>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> matchesIds = new ArrayList<>();
                        if (task.getResult().exists()) {
                            // Check if the "matches" field exists in the document
                            if (task.getResult().contains("matches")) {
                                matchesIds = (List<String>) task.getResult().get("matches");
                            }
                            // Fetch users only if there are match IDs
                            if (matchesIds != null) {
                                getUsersFromIds(matchesIds, callback);
                            } else {
                                // Handle the case where "matches" field is null or empty
                                Log.d(TAG, "No matches found for user: " + userId);
                                callback.onCallback(new ArrayList<>()); // Empty list as a result
                            }
                        } else {
                            // Handle the case where the document doesn't exist
                            Log.d(TAG, "User document does not exist for user: " + userId);
                            callback.onCallback(new ArrayList<>()); // Empty list as a result
                        }
                    } else {
                        // Log the error and invoke the callback with null
                        Log.e(TAG, "Error getting matches", task.getException());
                        callback.onCallback(null);
                    }
                });
    }


    private static void getUsersFromIds(List<String> userIds, final FirestoreCallback<List<User>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").whereIn(FieldPath.documentId(), userIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            User user = User.toObject(document.getData(), document.getId());

                            userList.add(user);
                        }
                        callback.onCallback(userList);
                    } else {
                        Log.e(TAG, "Error getting users from IDs", task.getException());
                        callback.onCallback(null);
                    }
                });
    }




    private static void getUserFromFirestore(String userId, final FirestoreCallback<User> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        callback.onCallback(user);
                    } else {
                        Log.e(TAG, "Error getting user", task.getException());
                        callback.onCallback(null);
                    }
                });
    }

    public interface FirestoreCallback<T> {
        void onCallback(T data);
    }
}
