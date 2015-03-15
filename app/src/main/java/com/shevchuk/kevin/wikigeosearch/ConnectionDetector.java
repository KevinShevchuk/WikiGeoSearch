package com.shevchuk.kevin.wikigeosearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Ravi Tamada from AndroidHive. Adapted for use in this project.
 *
 * Gathers information about the network connection and detects whether the device is connected to
 * the internet whether by data or Wifi.
 */

public class ConnectionDetector {

    private Context context;

    /**
     * Creates a connectionDetector Object
     *
     * @param context the context which is requesting the information
     */
    public ConnectionDetector(Context context)
    {
        this.context = context;
    }

    /**
     * Gathers the information regarding the network connection to determine whether the device is
     * connected to the internet or not.
     *
     * @return boolean
     *  false if the device is not connected to the internet.
     *  true if if the device is connected to the internet.
     */
    public boolean checkConnection()
    {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false; //No connection available
    }
}