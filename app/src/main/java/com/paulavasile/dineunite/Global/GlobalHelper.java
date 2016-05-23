package com.paulavasile.dineunite.Global;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulavasile.dineunite.MainActivity;
import com.paulavasile.dineunite.R;
import com.paulavasile.dineunite.data.FoundTableInfo;

import com.paulavasile.dineunite.fragments.SocialTableFragment;
import com.paulavasile.dineunite.task.AnyTask;
import com.paulavasile.dineunite.task.AnyTaskCallback;
import com.paulavasile.dineunite.utils.Utility;

import org.json.JSONObject;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by silve on 5/20/2016.
 */
public class GlobalHelper {

    public static final int PROFILE_BASE_REQUEST = 1200;
    public static final int SOCIAL_TABLE_JOIN_PROFILE = PROFILE_BASE_REQUEST + 1;
    public static final int SOCIAL_TABLE_CONTACT_PROFILE = PROFILE_BASE_REQUEST + 2;
    public static final int SOCIAL_TABLE_BOOK_PROFILE = PROFILE_BASE_REQUEST + 3;

    public static SocialTableFragment __gCurTable = null;

    public static boolean isConnectingToInternet(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static MainActivity __mainActivity = null;
    public static void regiserMe(MainActivity mainActivity) {
        __mainActivity = mainActivity;
    }

    public static MainActivity getMainActivity()
    {
        return __mainActivity;
    }
}
