package com.depressiontherapygame.Users.GameTetris.utils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

import timber.log.Timber;

/**
 * Created by agile-01 on 6/8/2017.
 * utility for network connection availibility, speed and type
 */
public class NetworkUtils {

    private final Context mContext;

    public NetworkUtils(Context context) {
        //no direct instances allowed. use di instead.
        mContext = context;
    }

    //region public methods

    /**
     * Checks if device is connected on network
     */
    public boolean isConnected() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isAvailable() && info.isConnected();
    }

    /**
     * Checks if there is slow connectivity
     * returns true in cases when there is 2g connection < 100kbPS
     */
    public boolean isConnectedOnSlowNetwork() {
        NetworkInfo info = getNetworkInfo();
        if (info == null || !info.isConnected()) {
            Timber.w("isConnectedOnSlowNetwork : device not connected to network. Check with isConnected() first!");
            return true;
        }
        return !hasFastConnection(info.getType(), info.getSubtype());
    }
    //endregion


    //region private internal methods

    /**
     * Get the network info
     */
    @Nullable
    private NetworkInfo getNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * Check if the connection is fast
     * returns true if connected on wifi
     * for mobile, it returns false for 2G connections (< 100 kbps)
     */
    private boolean hasFastConnection(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    Timber.w("hasFastConnection : Unrecognized sub type of network. Returning false");
                    return false;
            }
        } else {
            return false;
        }
    }
    //endregion


}