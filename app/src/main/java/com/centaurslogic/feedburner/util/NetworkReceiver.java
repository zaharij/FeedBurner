package com.centaurslogic.feedburner.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;
    private INetworkCallback networkCallback;
    private Context context;

    public interface INetworkCallback{
        void onWiFiAvailable();
        void onGSMAvailable();
        void onNoConnection();
    }

    public NetworkReceiver(INetworkCallback networkCallback, Context context){
        this.networkCallback = networkCallback;
        this.context = context;
    }

    public void registerReceiver(){
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            boolean isWiFiAvailable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            boolean isGSMAvailable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            if (isWiFiAvailable){
                networkCallback.onWiFiAvailable();
            } else if (isGSMAvailable){
                networkCallback.onGSMAvailable();
            }
        } else {
            networkCallback.onNoConnection();
        }
    }
}
