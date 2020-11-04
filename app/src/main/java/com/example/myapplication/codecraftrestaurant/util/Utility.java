package com.example.myapplication.codecraftrestaurant.util;

import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.common.api.GoogleApiClient;

public class Utility {


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }


}
