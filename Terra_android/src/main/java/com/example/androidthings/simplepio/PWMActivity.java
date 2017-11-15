/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.simplepio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.View;

import com.example.androidthings.simplepio.firebase.RecieveMessage;
import com.example.androidthings.simplepio.model.BackwardResponse;
import com.example.androidthings.simplepio.model.ForwardRequest;
import com.example.androidthings.simplepio.networking.SendData;
import com.example.androidthings.simplepio.sensors.MoistureSensor;
import com.example.androidthings.simplepio.singleton.Singletons;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


public class PWMActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public class TriggerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            init();
            discover();
            Log.d(TAG,"es triggered");
        }
    }
    private MoistureSensor mMoistureSensor;

    public static final String DEVICE_ID = "2";
    private View activity;
    private static final String TAG = PWMActivity.class.getSimpleName();
    private List<String> mVisitedIds;
    private String mUUID;
    private boolean mIsParent;
    private boolean mIsDiscoveryOn;
    private String mParentId;
    private TimerTask mBackPropTimer;

    // Parameters of the servo PWM
    /*private static final double MIN_ACTIVE_PULSE_DURATION_MS = 1;
    private static final double MAX_ACTIVE_PULSE_DURATION_MS = 2;
    private static final double PULSE_PERIOD_MS = 20;  // Frequency of 50Hz (1000/20)

    // Parameters for the servo movement over time
    private static final double PULSE_CHANGE_PER_STEP_MS = 0.2;
    private static final int INTERVAL_BETWEEN_STEPS_MS = 1000;

    private Handler mHandler = new Handler();
    private Pwm mPwm;
    private boolean mIsPulseIncreasing = true;
    private double mActivePulseDuration;*/
    private boolean mDidPing;

    private GoogleApiClient mGoogleApiClient;
    private ConnectionLifecycleCallback mConnectionLC;
    private ConnectionLifecycleCallback mBackwardLC;
    private ConnectivityManager connectivityManager;
    private List<BackwardResponse.MetricBean> mChildMetrics;
    private boolean mAllowDiscovery = true;
    private final SimpleArrayMap<Long, NotificationCompat.Builder> incomingPayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, NotificationCompat.Builder> outgoingPayloads = new SimpleArrayMap<>();


    private void init(){
        mIsDiscoveryOn=false;
        mIsParent=false;
        mParentId=null;
        mDidPing=false;
        mVisitedIds=new ArrayList<>();
        mChildMetrics=new ArrayList<>();
        mAllowDiscovery=true;
        if (mGoogleApiClient != null) {
            Nearby.Connections.stopDiscovery(mGoogleApiClient);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwm);
        activity = findViewById(R.id.activity_pwm);
        mUUID = UUID.randomUUID().toString();
        try {
            Log.d("kuwehw", FirebaseInstanceId.getInstance().getToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter statusIntentFilter = new IntentFilter(
                RecieveMessage.TRIGGER);
        TriggerReceiver triggerReceiver = new TriggerReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(triggerReceiver, statusIntentFilter);
        mMoistureSensor=new MoistureSensor();
        final String moistureData = mMoistureSensor.setUpSensor();
        activity=findViewById(R.id.activity_pwm);
        init();
        mUUID=UUID.randomUUID().toString();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
        mGoogleApiClient.connect();

        mConnectionLC = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(String s, ConnectionInfo connectionInfo) {
                Log.d(TAG, "Connection Initiated");
                Nearby.Connections.acceptConnection(
                        mGoogleApiClient, s, mPayloadCallback);
            }

            @Override
            public void onConnectionResult(String s, ConnectionResolution connectionResolution) {
                Log.d(TAG, "Connection Result");
                switch (connectionResolution.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        if (mIsParent) {
                            ForwardRequest request = new ForwardRequest(mVisitedIds, mUUID);

                            Gson gson = Singletons.getGson();
                            Nearby.Connections.
                                    sendPayload(mGoogleApiClient, s, Payload.fromBytes(gson.toJson(request).getBytes()));
                            activity.setBackgroundColor(getResources().getColor(R.color.red));
                        }
                        mIsParent = false;

                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        // The connection was rejected by one or both sides.
                        break;
                }
            }

            @Override
            public void onDisconnected(String s) {

            }
        };

        mBackwardLC = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(String s, ConnectionInfo connectionInfo) {
                Nearby.Connections.acceptConnection(
                        mGoogleApiClient, s, mPayloadCallback);
            }

            @Override
            public void onConnectionResult(String s, ConnectionResolution connectionResolution) {
                Log.d(TAG, "Backpropagating");

                BackwardResponse response = new BackwardResponse(mChildMetrics, mVisitedIds);
                response.insertMetricBean(DEVICE_ID, moistureData, "moisture");
                String responseText = Singletons.getGson().toJson(response);
                Nearby.Connections.sendPayload(mGoogleApiClient, s, Payload.fromBytes(responseText.getBytes()))
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Log.d(TAG,status.toString());
                                init();

                            }
                        });

            }

            @Override
            public void onDisconnected(String s) {

            }
        };


        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        PeripheralManagerService service = new PeripheralManagerService();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMoistureSensor.destroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "API Client connected");
        startAdvertising();


        if (mIsDiscoveryOn) {
            discover();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private boolean isConnectedToNetwork() {

        NetworkInfo info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo info1 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        return (info != null && info.isConnectedOrConnecting()) || (info1 != null && info1.isConnectedOrConnecting());
    }

    private final PayloadCallback mPayloadCallback =
            new PayloadCallback() {
                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    mVisitedIds.add(endpointId);
                    Log.d(TAG, new String(payload.asBytes()));
                    String payloadString = new String(payload.asBytes());
                    try {
                        JSONObject jsonObject = new JSONObject(payloadString);
                        String type = jsonObject.getString("type");
                        if (type.equals("forward")) {
                            ForwardRequest forwardRequest = Singletons.getGson().fromJson(payloadString, ForwardRequest.class);
                            mVisitedIds = forwardRequest.getPrevVisited();
                            mVisitedIds.add(endpointId);
                            mParentId = endpointId;
                            startDiscovery();
                        } else if (type.equals("backward")) {
                            BackwardResponse backwardResponse = Singletons.getGson().fromJson(payloadString, BackwardResponse.class);
                            mVisitedIds.addAll(backwardResponse.getPrevVisited());
                            Log.d(TAG, "Going back");
                            mChildMetrics.addAll(backwardResponse.getMetrics());

                            startDiscovery();
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, e.getLocalizedMessage());
                    }


                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {


                }
            };


    private void startAdvertising() {
        /*(if (!isConnectedToNetwork()) {
            Log.d(TAG, "startAdvertising: not connected to WiFi network.");
            return;
        }*/
        Nearby.Connections.startAdvertising(
                mGoogleApiClient,
                "test",
                "test",

                mConnectionLC,
                new AdvertisingOptions(Strategy.P2P_STAR))
                .setResultCallback(
                        new ResultCallback<Connections.StartAdvertisingResult>() {
                            @Override
                            public void onResult(@NonNull Connections.StartAdvertisingResult result) {
                                Log.d(TAG, result.getStatus().getStatus().toString());
                                if (result.getStatus().isSuccess()) {
                                    // We're advertising!
                                    activity.setBackgroundColor(getResources().getColor(R.color.yellow));
                                    Log.d(TAG, "Starting to advertise");
                                } else {
                                    // We were unable to start advertising.
                                }
                            }
                        });
    }


    private final EndpointDiscoveryCallback mEndpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(
                        final String endpointId, DiscoveredEndpointInfo discoveredEndpointInfo) {
                    // An endpoint was found!

                    if (mDidPing || !mAllowDiscovery) return;
                    mDidPing = false;
                    for (String id : mVisitedIds) {
                        if (endpointId.equals(id)) return;
                    }
                    mAllowDiscovery = false;

                    String name = "hemanth";
                    if (mBackPropTimer != null) mBackPropTimer.cancel();
                    Nearby.Connections.requestConnection(
                            mGoogleApiClient,
                            name,
                            endpointId,
                            mConnectionLC)
                            .setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            if (status.isSuccess()) {
                                                Log.d(TAG, "Asking to accept connection");
                                                mIsParent = true;
                                            } else {
                                                Log.d(TAG, "Failed to accept connection");
                                            }
                                        }
                                    });


                    Nearby.Connections.stopDiscovery(mGoogleApiClient);

                }

                @Override
                public void onEndpointLost(String endpointId) {
                    // A previously discovered endpoint has gone away.
                }
            };

    private void startDiscovery() {
        Log.d(TAG, "Google Api Client Status: " + mGoogleApiClient.isConnected());
        mGoogleApiClient.disconnect();
        mIsDiscoveryOn = true;
        mGoogleApiClient.connect();
    }

    private void discover() {
        Nearby.Connections.startDiscovery(
                mGoogleApiClient,
                "test",
                mEndpointDiscoveryCallback,
                new DiscoveryOptions(Strategy.P2P_STAR))
                .setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {

                                if (status.isSuccess()) {
                                    Log.d(TAG, "Starting Discovery");
                                    activity.setBackgroundColor(getResources().getColor(R.color.green));
                                    final Handler handler=new Handler();
                                    final Runnable runnable=new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.setBackgroundColor(getResources().getColor(R.color.yellow));
                                        }
                                    };

                                    mBackPropTimer = new TimerTask() {
                                        @Override
                                        public void run() {
                                            Nearby.Connections.stopDiscovery(mGoogleApiClient);
                                            handler.post(runnable);
                                            if(mParentId!=null){
                                                Nearby.Connections.requestConnection(
                                                        mGoogleApiClient,
                                                        "hemanth",
                                                        mParentId,
                                                        mBackwardLC)
                                                        .setResultCallback(
                                                                new ResultCallback<Status>() {
                                                                    @Override
                                                                    public void onResult(@NonNull Status status) {
                                                                        if (status.isSuccess()) {
                                                                            Log.d(TAG, "Asking to reconnect");
                                                                            mIsParent = true;
                                                                        } else {
                                                                            Log.d(TAG, "Failed to reconnect");
                                                                        }
                                                                    }
                                                                });
                                            }else{
                                                addDataToServer(mChildMetrics);
                                            }
                                        }
                                    };
                                    new Timer().schedule(mBackPropTimer,10000);
                                } else {

                                }
                            }
                        });
    }

    public void addDataToServer(List<BackwardResponse.MetricBean> list) {
        ParentClient client = new ParentClient("https://02bf0a80.ngrok.io");
        for (BackwardResponse.MetricBean bean : list) {
            double moisture;
            if (bean.data.equals("true")) {
                moisture = 1.0;
            } else {
                moisture = 0.0;
            }
            //client.addData(bean.id, 0.0, moisture);
        }
    }
}
