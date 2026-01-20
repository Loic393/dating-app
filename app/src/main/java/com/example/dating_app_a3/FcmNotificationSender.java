package com.example.dating_app_a3;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class FcmNotificationSender {
    public static void sendNotification(String deviceToken, String title, String body) {
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(deviceToken)
                .setMessageId(Integer.toString(1))
                .addData("title", title)
                .addData("body", body)
                .build());
    }

}
