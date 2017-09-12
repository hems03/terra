package com.example.hemanth.terra_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.hemanth.terra_android.common.BaseActivity
import com.google.android.gms.common.api.GoogleApiClient
import javax.inject.Inject

class NodeActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks {
    val TAG = "NodeActivity"

    @Inject
    lateinit var _googleApiClient: GoogleApiClient

    inner class TriggerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Traversal triggered")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getControllerComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node)
        _googleApiClient.registerConnectionCallbacks(this)
        _googleApiClient.registerConnectionFailedListener {
            _googleApiClient.connect()
        }
    }

    override fun onConnected(p0: Bundle?) {
        Log.d(TAG, "Google API Client connected")
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "Google API Client suspended")
    }
}
