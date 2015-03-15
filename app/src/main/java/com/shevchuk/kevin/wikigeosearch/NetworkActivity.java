package com.shevchuk.kevin.wikigeosearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Kevin on 3/14/2015.
 */
public class NetworkActivity extends Activity {

    protected static final String QUERY_URL = "http://en.wikipedia.org/w/api.php?action=query&format=json&list=geosearch&gsradius=10000&gscoord=";
    Context context = this;
    JSONArray geosearch;
    ProgressDialog mDialog;
    AlertDialog noResults;
    String latitude;
    String longitude;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_activity);
        Intent intent = getIntent();
        latitude = intent.getStringExtra("Latitude");
        longitude = intent.getStringExtra("Longitude");

        // Create the "Searching" dialog box.
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Searching...");
        mDialog.setCancelable(false);

        HTTPRequest(latitude, longitude);
    }

    /**
     * constructs the URL and sends and receives the HTTP request from the server. Uses
     * AsyncHTTPClient to handle the request.
     *
     * @param latitude the latitude as extracted from the Location data. Used to create the query.
     * @param longitude the longitude as extracted from the Location data. Used to create the query.
     */
    public void HTTPRequest(String latitude, String longitude) {
        //make URL from the base string and the coordinates
        String urlString = latitude + "|" + longitude;
        try {
            urlString = URLEncoder.encode(urlString, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // Notify the user is encoding fails.
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();

        mDialog.show();

        // Perform HTTP request using the URL.
        client.get(QUERY_URL + urlString,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        mDialog.dismiss();
                        // JSON array is inside another JSON object. Extract geosearch from query.
                        JSONObject query = jsonObject.optJSONObject("query");
                        geosearch = query.optJSONArray("geosearch");
                        if(geosearch.length() > 1) {
                            sendResult(geosearch);
                        }
                        else{

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                        mDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                        // Log error message
                        Log.e("URL failed", statusCode + " " + throwable.getMessage());
                    }
                });
    }
    private void sendResult(JSONArray result){
        Intent listIntent = new Intent(this, ListActivity.class);
        listIntent.putExtra("Latitude", latitude);
        listIntent.putExtra("Longitude", longitude);
        listIntent.putExtra("Results", result.toString());
        startActivity(listIntent);
    }
}
