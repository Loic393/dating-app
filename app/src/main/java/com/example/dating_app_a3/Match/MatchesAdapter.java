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
import com.example.dating_app_a3.Models.User;
import com.example.dating_app_a3.R;

import java.util.ArrayList;
import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchesViewHolder> {
    private List<User> matchList;
    private Context context; // Add this variable to hold the context

    public MatchesAdapter(Context context, List<User> matchesList) {
        this.context = context;
        this.matchList = matchesList != null ? matchesList : new ArrayList<>();
    }

    public void setMatches(List<User> matches) {
        this.matchList = matches != null ? matches : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create and return ViewHolder
        // Inflate your item layout for each item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolder holder, int position) {
        // Bind data to each item
        User user = matchList.get(position);
        // Set user information to the item views
        holder.textViewName.setText(user.getName());
        if (user.getProfilePictureUrls() != null && !user.getProfilePictureUrls().isEmpty()) {
            String imageUrl = user.getProfilePictureUrls().get(0); // Assuming the first URL
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.profilepic)) // Placeholder image while loading
                    .into(holder.imageViewProfile);
        } else {
            // If no profile picture URL is available, you can set a default image or hide the ImageView
            holder.imageViewProfile.setImageResource(R.drawable.profilepic);
        }
    }

    @Override
    public int getItemCount() {
        return matchList != null ? matchList.size() : 0;
    }

    class MatchesViewHolder extends RecyclerView.ViewHolder {
        // Declare your item views here
        // For example:
        TextView textViewName;
        ImageView imageViewProfile;

        public MatchesViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your item views here
            // For example:
            textViewName = itemView.findViewById(R.id.textViewName);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            // Set OnClickListener for the ImageView
            imageViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the user at the clicked position
                    User clickedUser = matchList.get(getAdapterPosition());

                    // Open the ChatActivity and pass relevant information
                    openChatActivity(clickedUser);
                }
            });
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
}
