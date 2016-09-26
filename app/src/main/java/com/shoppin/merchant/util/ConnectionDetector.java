package com.shoppin.merchant.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionDetector {

    private Context _context;
    boolean wifiConnected, mobileDataConnected;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    @SuppressLint("UseValueOf")
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();

            Log.v("Notification", "Network info : " + activeInfo);
            if (activeInfo != null && activeInfo.isConnected()) {

                wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
                mobileDataConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;

                if (wifiConnected || mobileDataConnected) {
                    Log.v("Notification", "is wifi or mobile is connected");

                    return true;
                }
                //return true;
            } else {
                wifiConnected = false;
                mobileDataConnected = false;

                return false;
            }


        return false;
    }

}
