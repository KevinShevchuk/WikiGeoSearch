package com.shevchuk.kevin.wikigeosearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnInfoWindowClickListener
{

    protected static final String URL_STRING = "http://en.m.wikipedia.com/wiki/";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLngBounds bounds;
    private String lat;
    private String lon;
    private JSONArray results;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
            {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap()
    {

        ArrayList<LatLng> latLngList = new ArrayList<LatLng>();

        //Place a marker for the users current position.
        Intent mapsIntent = getIntent();
        lat = mapsIntent.getStringExtra("Latitude");
        Double latitude = Double.parseDouble(lat);
        lon = mapsIntent.getStringExtra("Longitude");
        Double longitude = Double.parseDouble(lon);

        //Expand the bounds around the users location by a little bit and place a marker for them.
        LatLng ll = new LatLng(latitude, longitude);
        LatLng ltlgSW = new LatLng(latitude - .01, longitude - .01);
        LatLng ltlgNE = new LatLng(latitude + .01, longitude + .01);
        bounds = new LatLngBounds(ltlgSW, ltlgNE);
        mMap.addMarker(new MarkerOptions()
                .position(ll)
                .title("You are Here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //make markers for the other locations.
        String jsonArray = mapsIntent.getStringExtra("Results");
        latitude = null;
        longitude = null;
        String distance = null;

        try
        {
            results = new JSONArray(jsonArray);

            String pageTitle = null;
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject marker = results.getJSONObject(i);
                if (marker.has("title"))
                {
                    pageTitle = marker.optString("title");
                }

                if (marker.has("lat"))
                {
                    latitude = marker.optDouble("lat");
                }

                if (marker.has("lon"))
                {
                    longitude = marker.optDouble("lon");
                }

                if (marker.has("dist"))
                {
                    distance = marker.optString("dist");
                }

                //Add the coordinates to the bounds list.
                LatLng templl = new LatLng(latitude, longitude);
                latLngList.add(templl);

                mMap.addMarker(new MarkerOptions()
                        .position(templl)
                        .title(pageTitle))
                        .setSnippet(distance + " Meters");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //change the view to contain all the markers.
        for (int i = 0; i < latLngList.size(); i++)
        {
            bounds = bounds.including(latLngList.get(i));
        }

        //Hardcoded layout until I can figure out how to properly create a bounded view dynamically.
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1366, 768, 30));
        mMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_maps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
            case R.id.action_show_list:
                switchToListView();
                return true;
            case R.id.action_search:
                searchAgain();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when "Search Again" is selected from the Action Bar
     */
    private void searchAgain()
    {
        Intent locationIntent = new Intent(this, LocationActivity.class);
        startActivity(locationIntent);
    }

    /**
     * Called when "Switch to Map View" is selected from the Action Bar.
     */
    private void switchToListView()
    {
        Intent listIntent = new Intent(this, ListActivity.class);
        listIntent.putExtra("Latitude", lat);
        listIntent.putExtra("Longitude", lon);
        listIntent.putExtra("Results", results.toString());
        startActivity(listIntent);
    }

    /**
     * Requests the web page if the user clicks on the Info Window for the marker.
     *
     * @param marker
     */
    @Override
     public void onInfoWindowClick(Marker marker)
    {
        String url = marker.getTitle();
        if (url != "You are Here") // No article is available for this, so don't send a request for it.
        {
            url = createURL(url);
            Uri uri = Uri.parse(url);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(webIntent);
        }
    }

    /**
     * Builds the URL string to send to the browser. Changes spaces in the title into underscores.
     *
     * @param title title of the page to be added into the URL.
     * @return urlString: the completed URL that it sent to the browser.
     */
    private String createURL(String title){
        title = title.replaceAll(" ","_");
        String urlString = URL_STRING + title;
        return urlString;
    }
}
