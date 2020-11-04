package com.example.myapplication.codecraftrestaurant.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GpsReceiver extends BroadcastReceiver {
    private final LocationCallBack locationCallBack;

    /**
     * initializes receiver with callback
     *
     * @param iLocationCallBack Location callback
     */
    public GpsReceiver(LocationCallBack iLocationCallBack) {
        this.locationCallBack = iLocationCallBack;
    }

    /**
     * triggers on receiving external broadcast
     *
     * @param context Context
     * @param intent  Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            locationCallBack.onLocationTriggered();
        }
    }

    public interface LocationCallBack {
        /**
         * on Location switch triggered
         */
        void onLocationTriggered();
    }
}
