package com.dm.pricinggame.activity.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by linuxraju on 3/25/17.
 */
public class Api {

    //final static public String baseUrl = "http://intersop.com/isop/api/v1/"; // test
    final static public String baseUrl = "http://isopnepal.com/isop/api/v1/"; // live

    final static public String loginUrl = baseUrl + "member/login";


    //check for network
    public static boolean isInNetwork(Context ctx) {
        if (ctx == null)
            return false;

        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
