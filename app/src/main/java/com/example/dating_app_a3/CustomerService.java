package com.example.dating_app_a3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerService extends AppCompatActivity {

    String[] questionList = {
            "How do I create a profile?",
            "How can I change my profile information and photos?",
            "What is the swiping feature, and how does it work?",
            "How do I start a conversation with someone I've matched with?",
            "How can I reset my password if I forget it?"};
    AutoCompleteTextView autoCompleteTextView;
    TextView answerTextView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);

        autoCompleteTextView = findViewById(R.id.questionPropositionsList);
        adapter = new ArrayAdapter<String>(this, R.layout.list_questions, questionList);
        answerTextView = findViewById(R.id.answerTextView);

        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String question = adapter.getItem(position);
                Toast.makeText(CustomerService.this, "item : " + question, Toast.LENGTH_SHORT).show();
                String answer = "";
                switch (position) {
                    case 0:
                        answer = "To create a profile, download the app, click on the \"Register\" button, and follow the instructions to set up your profile with photos and a bio.";
                        break;
                    case 1:
                        answer = "Navigate to your profile, tap on the edit icon, and you can update your photos, bio, and other profile details.";
                        break;
                    case 2:
                        answer = "Swiping is SoulMatch's main feature. Swipe right to like someone, swipe left to pass. If both users swipe right, it's a match, and you can start chatting.";
                        break;
                    case 3:
                        answer = "Once you've matched, tap on the chat icon, and send a message to start the conversation. Be creative and respectful in your opening messages.";
                        break;
                    case 4:
                        answer = "On the login screen, tap on \"Forgot password?\" and follow the instructions to reset your password through the email associated with your account.";
                        break;
                    default:
                        break;
                }

                answerTextView.setText(answer);
                answerTextView.setVisibility(View.VISIBLE);
            }
        });


    }

    public void goBack(View v){
        finish();
    }
}