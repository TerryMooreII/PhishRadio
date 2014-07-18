package com.terrymoreii.phishradio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.terrymoreii.phishradio.model.Show;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmoore on 7/17/14.
 */
public class ShowsAdapter extends ArrayAdapter<Show> {

    public ShowsAdapter(Context context, List<Show> shows) {
        super(context, R.layout.list_item_shows, shows);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Show show = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_shows, parent, false);
        }
        // Lookup view for data population
        TextView tvDate = (TextView) convertView.findViewById(R.id.list_item_show_date_textview);
        TextView tvVenueName = (TextView) convertView.findViewById(R.id.list_item_show_venueName_textview);
        // Populate the data into the template view using the data object
        tvDate.setText(show.date);
        tvVenueName.setText(show.venueName);
        // Return the completed view to render on screen
        return convertView;
    }
}