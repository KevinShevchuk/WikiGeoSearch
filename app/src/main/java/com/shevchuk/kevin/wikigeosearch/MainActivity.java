package com.shevchuk.kevin.wikigeosearch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * @author Kevin Shevchuk on 3/12/15
 *
 * Handles the launch and pre-search of the application. MainActivity prompts a window before
 * searching to prevent unwanted usage of data should the app be accidently launched. Calls
 * connection detector to check for network connectivity before searching.
 */

public class MainActivity extends ActionBarActivity
{
    Context context = this;
    Button greetingYes;
    Button greetingNo;

    /**
     * When true, bypasses LocationActivity and uses hardcoded data to test functionality
     */
    Boolean locationEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        greetingYes = (Button) findViewById(R.id.greetingYesButton);
        greetingNo = (Button) findViewById(R.id.greetingNoButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles clicks on the Yes button. checks for network connectivity then launches the
     * LocationActivity if successful. Otherwise it does not launch.
     *
     * @param view
     */
    public void clickYes(View view) {
        Boolean status = checkNetworkStatus();
        if (status == true) {
            if (locationEnabled = true) {
                Intent locationIntent = new Intent(this, LocationActivity.class);
                startActivity(locationIntent);
            } else { //skip using location data. For debug and testing uses only.
                Intent networkIntent = new Intent(this, NetworkActivity.class);
                String latitude = new String("37.786971");
                String longitude = new String("-122.399677");
                networkIntent.putExtra("Latitude", latitude);
                networkIntent.putExtra("Longitude", longitude);
                startActivity(networkIntent);
            }
        }
        else
        {
            showAlertDialog();
        }
    }

    /**
     * Exit the program if the user does not want to search.
     *
     * @param view
     */
    public void clickNo(View view)
    {
        System.exit(0);
    }

    /**
     * Function to display an alert dialog if no network connection is found.
     * */
    public void showAlertDialog()
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("No Connection Available")
                .setMessage("Please enable a network connection or turn off airplane mode")
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, close
                        // current activity

                    }
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    /**
     * Checks the status of the internet connection. Calls the ConnectionDetector class.
     *
     * @return Boolean, true for available connection, false for unavailable connection.
     */

    private Boolean checkNetworkStatus()
    {
        ConnectionDetector cd = new ConnectionDetector(context);
        Boolean status = cd.checkConnection();
        return status;
    }
}
