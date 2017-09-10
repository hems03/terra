package com.example.androidthings.simplepio.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class RecieveMessage extends FirebaseMessagingService {
    public RecieveMessage() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("MESSAGE", "Payload: " + remoteMessage.getData());
    }
}
