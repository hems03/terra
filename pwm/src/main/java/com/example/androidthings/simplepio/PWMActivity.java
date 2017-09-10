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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import com.example.androidthings.simplepio.model.BackwardResponse;
import com.example.androidthings.simplepio.model.ForwardRequest;
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
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class PWMActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = PWMActivity.class.getSimpleName();
    private List<String> mVisitedIds;
    private String mUUID;
    private boolean mIsParent=false;
    private boolean mIsDiscoveryOn=false;
    private String mParentId;

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
    private ConnectivityManager connectivityManager;
    private List<BackwardResponse.MetricBean> mChildMetrics;
    private boolean mAllowDiscovery=true;
    private final SimpleArrayMap<Long, NotificationCompat.Builder> incomingPayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, NotificationCompat.Builder> outgoingPayloads = new SimpleArrayMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVisitedIds=new ArrayList<>();
        mChildMetrics=new ArrayList<>();
        mDidPing=false;
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
                Log.d(TAG,"Connection Initiated");
                Nearby.Connections.acceptConnection(
                        mGoogleApiClient, s, mPayloadCallback);
            }

            @Override
            public void onConnectionResult(String s, ConnectionResolution connectionResolution) {
                Log.d(TAG,"Connection Result");
                switch (connectionResolution.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        if(mIsParent){
                            ForwardRequest request=new ForwardRequest(mVisitedIds,mUUID );

                            Gson gson=Singletons.getGson();
                            Nearby.Connections.
                                    sendPayload(mGoogleApiClient, s, Payload.fromBytes(gson.toJson(request).getBytes()) );
                        }
                        mIsParent=false;

                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        // The connection was rejected by one or both sides.
                        break;
                }
            }

            @Override
            public void onDisconnected(String s) {
                Log.d(TAG, s);
            }
        };

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        PeripheralManagerService service = new PeripheralManagerService();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (mIsDiscoveryOn)discover();
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
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
                    Log.d(TAG,new String(payload.asBytes()));
                    String payloadString=new String(payload.asBytes());
                    try{
                        JSONObject jsonObject=new JSONObject(payloadString);
                        switch (jsonObject.getString("type")){
                            case "forward":{
                                ForwardRequest forwardRequest= Singletons.getGson().fromJson(payloadString,ForwardRequest.class);
                                mVisitedIds=forwardRequest.getPrevVisited();
                                mVisitedIds.add(endpointId);
                                mParentId=endpointId;
                                startDiscovery();
                                break;
                            }
                            case "backward":{
                                BackwardResponse backwardResponse=Singletons.getGson().fromJson(payloadString,BackwardResponse.class);
                                mChildMetrics.addAll(backwardResponse.getMetrics());
                                break;
                            }
                        }
                    }catch (JSONException e){
                        Log.d(TAG,e.getLocalizedMessage());
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

                    if(mDidPing||!mAllowDiscovery)return;
                    mDidPing=false;
                    for(String id:mVisitedIds){
                        if(endpointId.equals(id)) return;
                    }
                    mAllowDiscovery=false;

                    String name = "hemanth";
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
                                                mIsParent=true;
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
        Log.d(TAG,"Google Api Client Status: "+mGoogleApiClient.isConnected());
        mGoogleApiClient.disconnect();
        mIsDiscoveryOn=true;
        mGoogleApiClient.connect();
    }

    private void discover(){
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
                                    new Timer().schedule(new TimerTask() {
                                                             @Override
                                                             public void run() {
                                                                 Nearby.Connections.stopDiscovery(mGoogleApiClient);
                                                                 BackwardResponse backwardResponse=new BackwardResponse(mChildMetrics);
                                                                 String responseText=Singletons.getGson().toJson(backwardResponse);
                                                                 Nearby.Connections.sendPayload(mGoogleApiClient,mParentId,Payload.fromBytes(responseText.getBytes()));
                                                             }
                                                         },15000
                                    );
                                } else {
                                    // We were unable to start discovering.
                                }
                            }
                        });
    }
}
