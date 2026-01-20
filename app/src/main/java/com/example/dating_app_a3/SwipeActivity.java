package com.example.dating_app_a3;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dating_app_a3.Chat.ChatActivity;
import com.example.dating_app_a3.Match.MatchesActivity;
import com.example.dating_app_a3.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.util.ArrayList;
import java.util.List;

public class SwipeActivity extends AppCompatActivity implements SwipeAdapter.AdapterCallback{

    private CardStackView cardStackView;
    private CardStackLayoutManager layoutManager;
    private SwipeAdapter swipeAdapter;
    private FirebaseAuth mAuth;
    private String currentUserSex;
    private String preferredSex;
    private List<String> viewed;
    private String currentUserID;


    private SeekBar ageRangeSeekBar;
    private SeekBar minAgeSeekBar;
    private SeekBar heightRangeSeekBar;
    private SeekBar minHeightSeekBar;
    private TextView txtSelectedAgeRange;
    private TextView txtSelectedHeightRange;
    private TextView txtSelectedMinAge;
    private TextView txtSelectedMinHeight;
    private Button btnApplyFilter;
    private final int defaultMaxAge = 100;  // Default value
    private final int defaultMinAge = 0;  // Default value
    private final int defaultMaxHeight = 215;  // Default value
    private final int defaultMinHeight = 0;  // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);


        // Disable touch input during scrolling

        mAuth = FirebaseAuth.getInstance();


        cardStackView = findViewById(R.id.cardStackView);

        // Initialize CardStackLayoutManager
        layoutManager = new CardStackLayoutManager(this, cardStackListener);


        // Set up CardStackView
        layoutManager.setStackFrom(StackFrom.None);
        layoutManager.setVisibleCount(3); // Number of cards visible on the screen
        cardStackView.setLayoutManager(layoutManager);
        layoutManager.setCanScrollVertical(false);

        // Initialize and set the adapter
        swipeAdapter = new SwipeAdapter(this, new ArrayList<>(), this); // Initialize with an empty list
        cardStackView.setAdapter(swipeAdapter);

        ImageView filterBtn = findViewById(R.id.filter_btn);
        filterBtn.setOnClickListener(v -> showFilterPopup());
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Retrieve the user document from the "users" collection
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            preferredSex = documentSnapshot.getString("preferredSex");
                            currentUserSex = documentSnapshot.getString("gender");
                            viewed = (List<String>) documentSnapshot.get("viewed");
                            currentUserID = documentSnapshot.getId();
                            // Log the values for debugging
                            Log.d(TAG, "preferredSex: " + preferredSex);
                            Log.d(TAG, "currentUserSex: " + currentUserSex);

                            // Fetch and display users
                            fetchAndDisplayUsers(defaultMaxAge, defaultMaxHeight, defaultMinAge, defaultMinHeight);
                        }else {
                            Intent intent = new Intent(SwipeActivity.this, ProfileSetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to fetch the user document
                        Log.e(TAG, "Error fetching user document: " + e.getMessage());
                    });
        } else {
            // User is not signed in, navigate to the main activity or perform necessary actions
            Intent intent = new Intent(SwipeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        ImageView chat_btn = findViewById(R.id.chat_btn);
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SwipeActivity.this, MatchesActivity.class);
                startActivity(intent);
            }
        });





    }

    private CardStackListener cardStackListener = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction, float ratio) {
            // Handle card dragging
        }

        @Override
        public void onCardSwiped(Direction direction) {
            // Handle card swiped (left or right)
            if (direction == Direction.Right) {
                // User liked the profile
                rightSwipe();
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String token = task.getResult();
                        System.out.println(token);
                        String deviceToken = "deviceToken";
                        FcmNotificationSender.sendNotification(deviceToken, "Notification Title", "Notification body");
                    }
                });

            } else if (direction == Direction.Left) {
                // User disliked the profile
                leftSwipe();
            }
        }

        @Override
        public void onCardRewound() {
            // Handle card rewind (undo)
        }

        @Override
        public void onCardCanceled() {
            // Handle card canceled (not enough swipe distance)
        }

        @Override
        public void onCardAppeared(View view, int position) {
            // Handle card appeared in the view
        }

        @Override
        public void onCardDisappeared(View view, int position) {
            // Handle card disappeared from the view
        }
    };
    private void rightSwipe() {
        // Add the logic for right swipe (user liked the profile)
        // Update the user's data in Firebase (add the liked user to the likes list, etc.)

        // Example: Get the liked user from the adapter
        User likedUser = swipeAdapter.getTopItem();
        if (likedUser != null) {
            // Add the liked user's ID to the likes list in the current user's data
            addLikedUserToCurrentUser(likedUser.getId());
            // Optional: Update the viewed list to ensure the user won't see the same profile again
            addViewedUserToCurrentUser(likedUser.getId());
            viewed.add(likedUser.getId());
            if(likedUser.getLikes() != null && likedUser.getLikes().contains(currentUserID)){
                Toast.makeText(SwipeActivity.this, "It's a match!", Toast.LENGTH_SHORT).show();
                addMatchedUserToDB(currentUserID, likedUser.getId());
                addMatchedUserToDB(likedUser.getId(), currentUserID);
            }
            swipeAdapter.removeTopItem();
        }
    }

    private void leftSwipe(){
        User unlikedUser = swipeAdapter.getTopItem();
        if (unlikedUser != null) {
            // Optional: Update the viewed list to ensure the user won't see the same profile again
            addViewedUserToCurrentUser(unlikedUser.getId());
            viewed.add(unlikedUser.getId());
            swipeAdapter.removeTopItem();
        }
    }

    private void fetchAndDisplayUsers(int maxAge, int maxHeight, int minAge, int minHeight) {
        // Fetch users from Firestore and update the adapter
        FirestoreUtils.getUsersFromFirestore(new FirestoreUtils.FirestoreCallback<List<User>>() {
            @Override
            public void onCallback(List<User> userList) {
                if (userList != null) {
                    // Log the size of the retrieved user list
                    Log.d(TAG, "Retrieved user list size: " + userList.size());

                    // Filter and update the adapter with the retrieved users
                    List<User> filteredUserList = filterUsers(userList, maxAge, maxHeight, minAge, minHeight);

                    // Log the size of the filtered user list
                    Log.d(TAG, "Filtered user list size: " + filteredUserList.size());

                    swipeAdapter.setUsers(filteredUserList);
                } else {
                    // Handle error or show a message
                    Toast.makeText(SwipeActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to fetch users");
                }
            }
        });
    }
    private List<User> filterUsers(List<User> userList, int maxAge, int maxHeight, int minAge, int minHeight) {
        List<User> filteredList = new ArrayList<>();

        // Get the current user's preferred sex
        String currentUserPreferredSex = preferredSex != null ? preferredSex : "unknown";
        Log.d(TAG, "current user's preferred sex: " + currentUserPreferredSex);
        Log.d(TAG, "Selected max age: " + maxAge);
        Log.d(TAG, "Selected max height: " + maxHeight);

        // Filter users based on the current user's preferred sex and viewed list
        for (User user : userList) {
            String userSex = user.getGender() != null ? user.getGender() : "unknown";
            String userPreferredSex = user.getPreferredSex() != null ? user.getPreferredSex() : "unknown";

            // Check if the user's ID is not in the viewed list
            boolean notInViewedList = viewed == null || !viewed.contains(user.getId());

            if(minAge <= user.getAge() && user.getAge() <= maxAge && minHeight <= user.getHeight() && user.getHeight() <= maxHeight){
                if ((notInViewedList && !user.getId().equals(currentUserID))) {
                    if(currentUserSex.equals(userPreferredSex) && currentUserPreferredSex.equals(userSex)){
                        filteredList.add(user);
                    } else if (currentUserSex.equals(userPreferredSex) && currentUserPreferredSex.equals("Both")) {
                        filteredList.add(user);
                    } else if (currentUserPreferredSex.equals("Both") && userPreferredSex.equals("Both")) {
                        filteredList.add(user);
                    } else if (userPreferredSex.equals("Both") && currentUserPreferredSex.equals(userSex)) {
                        filteredList.add(user);
                    }

                }
            }
            Log.d(TAG, "userName: " + user.getName());
            Log.d(TAG, "userSex: " + userSex + ", userPreferredSex: " + userPreferredSex);
            Log.d(TAG, "userID: " + user.getId());
            Log.d(TAG, "userAge: " + user.getAge());
            Log.d(TAG, "userHeight: " + user.getHeight());

            Log.d(TAG, "userBirthday: " + user.getBirthday());

        }

        return filteredList;
    }


    public void signOut(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(SwipeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void profile(View view){
        Intent intent = new Intent(SwipeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void addLikedUserToCurrentUser(String likedUserId) {
        // Add the liked user's ID to the likes list in the current user's data in Firebase
        // Example: Assuming you have a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Assuming "likes" is the field in the user document
            db.collection("users").document(currentUserId).update("likes", FieldValue.arrayUnion(likedUserId))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Liked user added to likes list"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding liked user to likes list: " + e.getMessage()));
        }
    }

    private void addViewedUserToCurrentUser(String viewedUserId) {
        // Add the viewed user's ID to the viewed list in the current user's data in Firebase
        // Example: Assuming you have a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Assuming "viewed" is the field in the user document
            db.collection("users").document(currentUserId).update("viewed", FieldValue.arrayUnion(viewedUserId))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Viewed user added to viewed list"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding viewed user to viewed list: " + e.getMessage()));
        }
    }

    private void addMatchedUserToDB(String matchedUserIdTarget, String matchedUserId) {
        // Add the viewed user's ID to the viewed list in the current user's data in Firebase
        // Example: Assuming you have a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Assuming "viewed" is the field in the user document
            db.collection("users").document(matchedUserIdTarget).update("matches", FieldValue.arrayUnion(matchedUserId))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Viewed user added to viewed list"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding viewed user to viewed list: " + e.getMessage()));
        }
    }


    @Override
    public void onDataUpdated() {
        User topItem = swipeAdapter.getTopItem();
    }

    @Override
    public void onItemClick(int position, User user) {
        // Handle the click event here
        // For example, you can show a dialog or start a new activity to display user information
        showUserDetailsDialog(user);
    }

    private void showUserDetailsDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(user.getName());
        builder.setMessage("Birthday: " + user.getBirthday() + "\nAbout me: \n" + user.getBiography());

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle OK button click if needed
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void goToCustomerService(View v){
        Intent intent = new Intent(SwipeActivity.this, CustomerService.class);
        startActivity(intent);
    }

    private void showFilterPopup() {
        // Inflate the popup_filter.xml layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_filter, null);

        // Create the popup window
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Initialize views from the popup_filter layout
        ageRangeSeekBar = popupView.findViewById(R.id.ageRangeSeekBar);
        minAgeSeekBar = popupView.findViewById(R.id.minAgeRangeSeekBar);
        heightRangeSeekBar = popupView.findViewById(R.id.heightRangeSeekBar);
        minHeightSeekBar = popupView.findViewById(R.id.minHeightRangeSeekBar);
        btnApplyFilter = popupView.findViewById(R.id.btnApplyFilter);

        // Corrected code: Find the TextViews in the popupView
        txtSelectedAgeRange = popupView.findViewById(R.id.txtSelectedAgeRange);
        txtSelectedHeightRange = popupView.findViewById(R.id.txtSelectedHeightRange);
        txtSelectedMinAge = popupView.findViewById(R.id.txtSelectedAgeMin);
        txtSelectedMinHeight = popupView.findViewById(R.id.txtSelectedHeightMin);
        // Set listeners for SeekBar changes
        ageRangeSeekBar.setOnSeekBarChangeListener(onAgeSeekBarChangeListener);
        minAgeSeekBar.setOnSeekBarChangeListener(onMinAgeSeekBarChangeListener);
        heightRangeSeekBar.setOnSeekBarChangeListener(onHeightSeekBarChangeListener);
        minHeightSeekBar.setOnSeekBarChangeListener(onMinHeightSeekBarChangeListener);



        // Set listener for the Apply Filter button
        btnApplyFilter.setOnClickListener(v -> {
            applyFilter();
            popupWindow.dismiss(); // Dismiss the popup after applying the filter
        });

        // Show the popup window
        popupWindow.showAtLocation(cardStackView, Gravity.CENTER, 0, 0);
    }
    private void applyFilter() {
        // Get the selected age and height range
        int selectedMaxAge = ageRangeSeekBar.getProgress();
        int selectedMaxHeight = heightRangeSeekBar.getProgress();
        int selectedMinAge = minAgeSeekBar.getProgress();
        int selectedMinHeight = minHeightSeekBar.getProgress();



        if(selectedMinAge <= selectedMaxAge && selectedMinHeight <= selectedMaxHeight ){
//            if (selectedMaxHeight == 0 && selectedMaxAge != 0) {
//                fetchAndDisplayUsers(selectedMaxAge, defaultMaxHeight, selectedMinAge, selectedMinHeight);
//            } else if (selectedMaxAge == 0 && selectedMaxHeight != 0) {
//                fetchAndDisplayUsers(defaultMaxAge, selectedMaxHeight, selectedMinAge, selectedMinHeight);
//            } else if (selectedMaxHeight == 0 && selectedMaxAge == 0) {
//                fetchAndDisplayUsers(defaultMaxAge, defaultMaxHeight, selectedMinAge, selectedMinHeight);
//            } else {
                fetchAndDisplayUsers(selectedMaxAge, selectedMaxHeight, selectedMinAge, selectedMinHeight);
//            }
        }else {
            Toast.makeText(SwipeActivity.this, "Please select a valid range! (Min < Max)", Toast.LENGTH_LONG).show();
        }
    }


    private SeekBar.OnSeekBarChangeListener onAgeSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Handle age range change
            String maxAge = "Max age: " + progress;
            txtSelectedAgeRange.setText(maxAge);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }
    };
    private SeekBar.OnSeekBarChangeListener onMinAgeSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Handle age range change
            String minAge = "Min age: " + progress;
            txtSelectedMinAge.setText(minAge);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }
    };

    private SeekBar.OnSeekBarChangeListener onHeightSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Handle height range change
            String maxHeight = "Max height: " + progress;
            txtSelectedHeightRange.setText(maxHeight);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }
    };

    private SeekBar.OnSeekBarChangeListener onMinHeightSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Handle height range change
            String minHeight = "Min height: " + progress;
            txtSelectedMinHeight.setText(minHeight);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Not needed for this example
        }
    };


}
