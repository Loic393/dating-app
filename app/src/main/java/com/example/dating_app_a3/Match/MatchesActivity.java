package com.example.dating_app_a3.Match;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dating_app_a3.Chat.ChatMessage;
import com.example.dating_app_a3.FirestoreUtils;
import com.example.dating_app_a3.Models.User;
import com.example.dating_app_a3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

// MatchActivity.java
public class MatchesActivity extends AppCompatActivity {

    private List<User> matchList;
    private ChatsAdapter chatsAdapter;
    private List<ChatMessage> messagesList;
    private MatchesAdapter matchesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        // Initialize and set up RecyclerView for matches
        RecyclerView recyclerViewMatches = findViewById(R.id.recyclerViewMatches);
        LinearLayoutManager layoutManagerMatches = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewMatches.setLayoutManager(layoutManagerMatches);
        matchesAdapter = new MatchesAdapter(this, matchList);
        recyclerViewMatches.setAdapter(matchesAdapter);



        RecyclerView recyclerViewConversations = findViewById(R.id.recyclerViewChats);
        LinearLayoutManager layoutManagerConversations = new LinearLayoutManager(this);
        recyclerViewConversations.setLayoutManager(layoutManagerConversations);
        chatsAdapter = new ChatsAdapter(this, messagesList);
        recyclerViewConversations.setAdapter(chatsAdapter);


        fetchMatchesFromFirestore();

    }

    private void fetchMatchesFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            FirestoreUtils.getMatchesFromFirestore(userId, new FirestoreUtils.FirestoreCallback<List<User>>() {
                @Override
                public void onCallback(List<User> matches) {
                    if (matches != null) {
                        matchList = matches;
                        matchesAdapter.setMatches(matches);
                        fetchLatestChatMessages();
                    } else {
                        Toast.makeText(MatchesActivity.this, "Failed to fetch matches", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void fetchLatestChatMessages() {
        messagesList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        // Iterate through each match and fetch the latest message
        for (User match : matchList) {
            String otherUserId = match.getId();
            FirebaseFirestore.getInstance().collection("chats")
                    .document(userId)
                    .collection(otherUserId)
                    .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            ChatMessage latestMessage = documentSnapshot.toObject(ChatMessage.class);
                            latestMessage.setSender(match);
                            messagesList.add(latestMessage);
                        }

                        // Check if all matches have been processed
                        if (counter.incrementAndGet() == matchList.size()) {
                            // Sort the messagesList based on timestamp before setting it in the adapter
                            Collections.sort(messagesList, (message1, message2) ->
                                    Long.compare(message2.getTimestamp(), message1.getTimestamp()));

                            // Update the adapter with the fetched messages
                            chatsAdapter.setChatMessages(messagesList);
                        }
                    });
        }
    }






}


