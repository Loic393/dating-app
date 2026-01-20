package com.example.dating_app_a3;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dating_app_a3.Models.User;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyakaido.android.cardstackview.CardStackView;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dating_app_a3.Models.User;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yuyakaido.android.cardstackview.CardStackView;

import org.w3c.dom.Text;

import java.util.List;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.ViewHolder> {

    private List<User> users;
    private Context context;
    private AdapterCallback callback;

    public SwipeAdapter(Context context, List<User> userList, AdapterCallback callback) {
        this.context = context;
        this.users = userList;
        this.callback = callback;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
        if (callback != null) {
            callback.onDataUpdated();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.nameTextView.setText(user.getName());
//        String birthday = user.getBirthday();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//        LocalDate birthdate = LocalDate.parse(birthday, formatter);
//
//        LocalDate currentDate = LocalDate.now();
//        Period age = Period.between(birthdate, currentDate);
//        String ageString = String.valueOf(age.getYears());

        holder.ageTextView.setText(String.valueOf(user.getAge()));


        if (user.getHeight() > 0){
            String heightStr = user.getHeight() + " cm";
            holder.heightTextView.setText(heightStr);
        }
        holder.bioTextView.setText(user.getBiography());


        // Load image using Glide
        if (user.getProfilePictureUrls() != null && !user.getProfilePictureUrls().isEmpty()) {
            String imageUrl = user.getProfilePictureUrls().get(0); // Assuming the first URL
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.profilepic)) // Placeholder image while loading
                    .into(holder.profileImageView);
        } else {
            // If no profile picture URL is available, you can set a default image or hide the ImageView
            holder.profileImageView.setImageResource(R.drawable.profilepic);
        }

        // Set OnClickListener for the CardView
        holder.cardView.setOnClickListener(v -> {
            if (callback != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    callback.onItemClick(adapterPosition, user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends CardStackView.ViewHolder {
        ImageView profileImageView;
        TextView nameTextView;
        TextView ageTextView;
        TextView bioTextView;
        TextView heightTextView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
            bioTextView = itemView.findViewById(R.id.biography);
            heightTextView = itemView.findViewById(R.id.heightTextView);
        }
    }

    public User getTopItem() {
        if (!users.isEmpty()) {
            return users.get(0); // Assuming the top item is at position 0
        }
        return null;
    }

    public void removeTopItem() {
        if (!users.isEmpty()) {
            users.remove(0);
            notifyDataSetChanged();
        }
    }

    public interface AdapterCallback {
        void onDataUpdated();
        void onItemClick(int position, User user);
    }


}


