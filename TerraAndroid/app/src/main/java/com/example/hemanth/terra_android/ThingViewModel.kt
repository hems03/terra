package com.example.hemanth.terra_android

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.util.Log
import com.example.hemanth.terra_android.model.BackwardResponse
import com.example.hemanth.terra_android.model.ForwardRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import javax.inject.Inject
import kotlin.properties.Delegates
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * Created by dev on 9/13/17.
 */
class ThingViewModel:ViewModel(),GoogleApiClient.ConnectionCallbacks{

    val TAG="ThingViewModel"
    val DISCOVERY_ID="TEST"
    val DEVICE_NAME=UUID.randomUUID().toString()



    @Inject
    lateinit var _googleApiClient: GoogleApiClient

    @Inject
    lateinit var _gson:Gson

    class State{
         var isAdvertising=false
         var isDiscovering=false
         var isChildrenTraversing=false
    }

    enum class TraversalState(state:Int){
        PARENT(0),
        CHILD(1)
    }

    private var _state:MutableLiveData<State> =MutableLiveData<State>()
    private var _traversalState:TraversalState by Delegates.notNull()
    private var _visitedIDs=ArrayList<String>()
    private var _childMetrics=ArrayList<BackwardResponse.MetricBean>()

    private var _parentId:String?=null

    fun getMyState():LiveData<State>{
        return _state
    }

    fun init(){
        _googleApiClient.registerConnectionCallbacks(this)
        _googleApiClient.registerConnectionFailedListener {
            _googleApiClient.connect()
        }
        _traversalState=TraversalState.CHILD

    }

    fun startDiscovery(){
        Nearby.Connections.startDiscovery(_googleApiClient,DISCOVERY_ID,_endpointLC, DiscoveryOptions(Strategy.P2P_STAR))
                .setResultCallback {
                    if(it.isSuccess){
                        val newState=_state.value
                        newState?.isDiscovering=true
                        _state.setValue(newState)


                    }
                }
    }

    override fun onConnected(p0: Bundle?) {
        Log.d(TAG, "Google API Client connected")
    }

    override fun onConnectionSuspended(p0: Int) {

    }




    var __connectionLC=object :ConnectionLifecycleCallback(){
        override fun onConnectionResult(p0: String?, p1: ConnectionResolution?) {
            when (p1?.getStatus()?.getStatusCode()) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    // We're connected! Can now start sending and receiving data.
                    if (_traversalState==TraversalState.PARENT) {
                        val request = ForwardRequest(_visitedIDs)
                        Nearby.Connections.sendPayload(_googleApiClient, p0, Payload.fromBytes(_gson.toJson(request).toByteArray()))
                    }
                    _traversalState=TraversalState.CHILD
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                }
            }
        }

        override fun onDisconnected(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onConnectionInitiated(p0: String?, p1: ConnectionInfo?) {
            Log.d(TAG, "Connection Initiated");
            Nearby.Connections.acceptConnection(
                    _googleApiClient, p0, _payloadCallback);

        }
    }

    var _endpointLC=object :EndpointDiscoveryCallback(){
        override fun onEndpointFound(p0: String?, p1: DiscoveredEndpointInfo?) {
            val visited=_visitedIDs.contains(p0)
            if(visited) return


            Nearby.Connections.requestConnection(_googleApiClient,DEVICE_NAME,p0,__connectionLC)
                    .setResultCallback {
                        Log.d(TAG,it.statusMessage)
                        if(it.isSuccess){
                            _traversalState=TraversalState.PARENT
                        }
                    }
            Nearby.Connections.stopDiscovery(_googleApiClient)
        }

        override fun onEndpointLost(p0: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    var _payloadCallback=object :PayloadCallback(){
        override fun onPayloadReceived(p0: String, p1: Payload) {
            val payloadString= String(p1.asBytes()!!)
            Log.d(TAG, String(p1.asBytes()!!))
            try {
                val jsonObject = JSONObject(payloadString)
                val type = jsonObject.getString("type")
                if (type == "forward") {
                    val forwardRequest = _gson.fromJson(payloadString, ForwardRequest::class.java)
                    _visitedIDs = ArrayList(forwardRequest.getPrevVisited())
                    _visitedIDs.add(p0)
                    _parentId = p0
                } else if (type == "backward") {
                    val backwardResponse = _gson.fromJson(payloadString, BackwardResponse::class.java)
                    _visitedIDs.addAll(backwardResponse.getPrevVisited())
                    Log.d(TAG, "Going back")
                    _childMetrics.addAll(backwardResponse.getMetrics())
                }
                startDiscovery()
            } catch (e: JSONException) {
                Log.d(TAG, e.localizedMessage)
            }


        }

        override fun onPayloadTransferUpdate(p0: String?, p1: PayloadTransferUpdate?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    
}