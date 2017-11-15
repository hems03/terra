package com.example.hemanth.terra_android.firebase;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by hemanth on 9/12/17.
 */

public class FirebaseService extends FirebaseMessagingService {
    public static final String TRIGGER="TRIGGER";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("MESSAGE", "Payload: " + remoteMessage.getData());
        Intent intent=new Intent(TRIGGER);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
