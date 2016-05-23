package com.paulavasile.dineunite.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.paulavasile.dineunite.utils.NetworkUtil;

/**
 * Created by silve on 5/20/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);

        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}
