package com.example.hemanth.terra_android

import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.hemanth.terra_android.common.BaseActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import javax.inject.Inject
import kotlin.properties.Delegates
import android.support.v4.content.LocalBroadcastManager
import com.example.hemanth.terra_android.NodeActivity.TriggerReceiver
import com.example.hemanth.terra_android.firebase.FirebaseService.TRIGGER
import android.content.IntentFilter
import com.example.hemanth.terra_android.dagger.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.activity_node.*


class NodeActivity : BaseActivity() {
    val TAG = "NodeActivity"


    private var viewModel: ThingViewModel by Delegates.notNull()

    inner class TriggerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Traversal triggered")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getControllerComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_node)
        viewModel = ViewModelProviders.of(this, MyViewModelFactory(application as App)).get(ThingViewModel::class.java)



        viewModel.getMyState().observeForever {
            if (it != null) {
                if (it.isDiscovering) {
                    activity_node.setBackgroundColor(resources.getColor(R.color.green))
                } else if (it.isChildrenTraversing) {
                    activity_node.setBackgroundColor(resources.getColor(R.color.red))
                } else if (it.isAdvertising) {
                    activity_node.setBackgroundColor(resources.getColor(R.color.yellow))
                } else {
                    activity_node.setBackgroundColor(resources.getColor(R.color.colorAccent))
                }
            }
        }

        val statusIntentFilter = IntentFilter(
                "TRIGGER")
        val triggerReceiver = TriggerReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(triggerReceiver, statusIntentFilter)
    }

}
