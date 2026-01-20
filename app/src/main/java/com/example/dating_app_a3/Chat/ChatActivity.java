package com.example.dating_app_a3.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dating_app_a3.LoginActivity;
import com.example.dating_app_a3.Match.MatchesActivity;
import com.example.dating_app_a3.R;
import com.example.dating_app_a3.SwipeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messageRecyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private String receiverUserId; // The user ID of the person you're chatting with
    private String receiverUserName;
    private String currentUserId; // The current user's ID
    private FirebaseFirestore firestore;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firestore = FirebaseFirestore.getInstance();
        receiverUserId = "userId"; // Set the receiver's user ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("receiverUserId")) {
            receiverUserId = intent.getStringExtra("receiverUserId");
        }
        if (intent != null && intent.hasExtra("receiverUserName")) {
            receiverUserName = intent.getStringExtra("receiverUserName");
        }
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the title of the Toolbar
        //if (receiverUserId != null) {
        //    toolbar.setTitle("Chat with " + receiverUserName);
        //}

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        TextView textView = findViewById(R.id.nameTextView);
        textView.setText(receiverUserName);

        messageRecyclerView = findViewById(R.id.messageRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages, currentUserId);

        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageEditText.setText("");
            }
        });

        loadMessages();
    }

    private void loadMessages() {
        Query query = firestore.collection("chats")
                .document(currentUserId)
                .collection(receiverUserId)
                .orderBy("timestamp", Query.Direction.ASCENDING);

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle error
                return;
            }

            for (DocumentChange dc : value.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        ChatMessage message = dc.getDocument().toObject(ChatMessage.class);
                        messages.add(message);
                        adapter.notifyDataSetChanged();
                        break;
                    // Handle modified or removed if needed
                }
            }

            // Scroll to the last message
            if (!messages.isEmpty()) {
                messageRecyclerView.smoothScrollToPosition(messages.size() - 1);
            }
        });
    }

    private void sendMessage(String messageText) {
        ChatMessage message = new ChatMessage(messageText, currentUserId, System.currentTimeMillis());

        firestore.collection("chats")
                .document(currentUserId)
                .collection(receiverUserId)
                .add(message);

        firestore.collection("chats")
                .document(receiverUserId)
                .collection(currentUserId)
                .add(message);
    }
    public void backButton(View view){
        finish();
    }
}

