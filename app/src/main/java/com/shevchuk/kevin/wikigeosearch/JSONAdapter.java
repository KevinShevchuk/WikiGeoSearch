package com.shevchuk.kevin.wikigeosearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Kevin Shevchuk on 3/13/2015.
 *
 * Handles the loading of the JSONArray data in to the ListView to display to the user. Dynamically
 * loads the data into the list objects as the list scrolls.
 */
public class JSONAdapter extends BaseAdapter {

    // Tag for logging.
    private static final String TAG = "JSONAdapter";

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    /**
     * Constructor.
     *
     * @param context current context.
     * @param inflater used to stretch the layout to house the data.
     */
    public JSONAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    @Override
    public int getCount()
    {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position)
    {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Populates the List view with the new data and sets up the ViewHolder to hold the data when
     * the item is not visible in the list view.
     *
     * @param position position in the listView.
     * @param convertView the ListView to display the data in.
     * @param parent the ViewGroup in which the ListView is present in.
     *
     * @return the ListView itself, which is displayed to the user.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        // check if the view already exists
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.parsed_json_list, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.pageTitleView = (TextView) convertView.findViewById(R.id.page_title);
            holder.latlongView = (TextView) convertView.findViewById(R.id.text_lat_long);
            holder.distanceView = (TextView) convertView.findViewById(R.id.text_distance);

            // hang onto this holder for future recycling
            convertView.setTag(holder);
        }
        else
        {
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // Populate the View with the JSON data.

        JSONObject jsonObject = (JSONObject) getItem(position);

        String pageTitle = "Default";
        String latitude = "0.0";
        String longitude = "0.0";
        String distance = "1.0";

        if (jsonObject.has("title"))
        {
            pageTitle = jsonObject.optString("title");
        }

        if (jsonObject.has("lat"))
        {
            latitude = jsonObject.optString("lat");
        }

        if (jsonObject.has("lon"))
        {
            longitude = jsonObject.optString("lon");
        }

        if (jsonObject.has("dist"))
        {
            distance = jsonObject.optString("dist");
        }

        // Send these Strings to the TextViews for display
        holder.pageTitleView.setText(pageTitle);

        // Add special characters to coordinates.
        holder.latlongView.setText(latitude + "\u00B0,"+ longitude + "\u00B0");
        holder.distanceView.setText(distance + " Meters");

        return convertView;
    }

    /**
     * Updates the data present in the ViewHolder when a new set of data has come in.
     *
     * @param jsonArray the new data set to update with.
     */
    public void updateData(JSONArray jsonArray)
    {
        // update the adapter's dataset
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    /**
     * Class to hold the data about each web page when they are not visible in the List view.
     */
    private static class ViewHolder
    {
        public TextView pageTitleView;
        public TextView latlongView;
        public TextView distanceView;
    }
}

