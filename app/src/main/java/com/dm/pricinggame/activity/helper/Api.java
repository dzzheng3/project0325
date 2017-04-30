package com.dm.pricinggame.activity.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by linuxraju on 3/25/17.
 */
public class Api {

    //final static public String baseUrl = "http://intersop.com/isop/api/v1/"; // live
    final static public String baseUrl = "http://rajutest.isopnepal.com/api/"; // test

    final static public String loginUrl = baseUrl + "login/";
    final static public String registerUrl = baseUrl+"users";
    final static public String listOfGameUrl = baseUrl+"games";
    final static public String GameInitializeUrl = baseUrl+"player-game";
    final static public String submitValueUrl = baseUrl+"score";
    final static public String runningGameUrl = baseUrl+"running-game";
    final static public String AvailableGameUrl = baseUrl+"available-games";
    final static public String nextLevelUrl = baseUrl+"change-level";
    final static public String completedGameUrl = baseUrl+"player-game-history";
    final static public String decisionGameUrl = baseUrl+"decision-game";
    final static public String submitDecisionValue = baseUrl+"submit-decision";


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
