package com.example.dating_app_a3.Match;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dating_app_a3.Chat.ChatActivity;
import com.example.dating_app_a3.Chat.ChatMessage;
import com.example.dating_app_a3.Models.User;
import com.example.dating_app_a3.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;
    private Context context;

    public ChatsAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages != null ? chatMessages : new ArrayList<>();
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages != null ? chatMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        // Set user information to the item views
        holder.textViewUserName.setText(message.getSender().getName());
        holder.textViewMessage.setText(message.getText());
        holder.textViewTimestamp.setText(formatTimestamp(message.getTimestamp()));



        if (message.getSender().getProfilePictureUrls() != null && !message.getSender().getProfilePictureUrls().isEmpty()) {
            String imageUrl = message.getSender().getProfilePictureUrls().get(0);
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.profilepic)) // Placeholder image while loading
                    .into(holder.imageViewProfile);
        } else {
            // If no profile picture URL is available, you can set a default image or hide the ImageView
            holder.imageViewProfile.setImageResource(R.drawable.profilepic);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChatActivity(message.getSender());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile;
        TextView textViewUserName;
        TextView textViewMessage;
        TextView textViewTimestamp;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
        }
    }

    private String formatTimestamp(long timestamp) {
        // Implement your logic to format the timestamp (e.g., convert to readable date/time)
        // For example, you can use SimpleDateFormat or Android's DateUtils.
        // Here's a simple example:

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void openChatActivity(User user) {
        // Create an Intent to launch the ChatActivity
        Intent intent = new Intent(context, ChatActivity.class);

        // Pass relevant information to the ChatActivity (e.g., user ID)
        intent.putExtra("receiverUserId", user.getId());
        intent.putExtra("receiverUserName", user.getName());

        // Start the ChatActivity
        context.startActivity(intent);
    }
}


