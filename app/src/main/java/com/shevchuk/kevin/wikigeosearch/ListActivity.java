package com.shevchuk.kevin.wikigeosearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author Kevin Shevchuk on 3/13/2015.
 *
 * ListActivity handles the connection to the servers, parsing the JSON request received from the
 * servers and displaying the results in ListView for display. Uses the JSONAdapter class to handle
 * JSON request data.
 */
public class ListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener
{

    protected static final String QUERY_URL = "http://en.wikipedia.org/w/api.php?action=query&format=json&list=geosearch&gsradius=10000&gscoord=";
    protected static final String URL_STRING = "http://en.m.wikipedia.com/wiki/";

    ListView parsedListView;
    JSONAdapter mJSONAdapter;
    JSONArray results;
    String latitude;
    String longitude;

    /**
     * Standard launch of the ListActivity. this handles fetching the results from the Intent
     * object from the LocationActivity. It also provides the "Searching" dialog box while the
     * response is being parsed and displayed.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        latitude = intent.getStringExtra("Latitude");
        longitude = intent.getStringExtra("Longitude");
        try
        {
            results = new JSONArray(intent.getStringExtra("Results"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        // Set up the ListView and its JSONAdapter.
        parsedListView = (ListView) findViewById(R.id.parsed_list_view);
        mJSONAdapter = new JSONAdapter(this, getLayoutInflater());
        parsedListView.setAdapter(mJSONAdapter);
        mJSONAdapter.updateData(results);

        // Add the listener for each item in the ListView.
        parsedListView.setOnItemClickListener(this);
    }

    /**
     * Handles the clicks of items on the ListView. Extracts the title of the item in the list to
     * be used when constructing the URL to load the web page. The title is then sent along with the
     * intent to load the web page directly in the browser.
     *
     * @param parent AdapterView. In this case the one rendering the ListView with the JSON data.
     * @param view The current View rendering the page.
     * @param position Position data for the click.
     * @param id The id of the clicked on Object.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String title = ((TextView) view.findViewById(R.id.page_title)).getText().toString();
        String url = createURL(title);

        Uri uri = Uri.parse(url);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(webIntent);
    }

    /**
     * Builds the URL string to send to the browser. Changes spaces in the title into underscores.
     *
     * @param title title of the page to be added into the URL.
     * @return urlString: the completed URL that it sent to the browser.
     */
    private String createURL(String title)
    {
        title = title.replaceAll(" ","_");
        String urlString = URL_STRING + title;
        return urlString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_parse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
            case R.id.action_show_map:
                switchToMapsView();
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
    private void switchToMapsView()
    {
        Intent mapsIntent = new Intent(this, MapsActivity.class);
        mapsIntent.putExtra("Latitude", latitude);
        mapsIntent.putExtra("Longitude", longitude);
        mapsIntent.putExtra("Results", results.toString());
        startActivity(mapsIntent);
    }

    /**
     * Intercepts the back button to send the user all the way back to the beginning of the app.
     */
    @Override
    public void onBackPressed()
    {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }


}
