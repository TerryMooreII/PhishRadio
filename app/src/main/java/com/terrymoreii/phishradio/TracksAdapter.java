package com.terrymoreii.phishradio;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.terrymoreii.phishradio.model.Show;
import com.terrymoreii.phishradio.model.Track;

import java.util.List;

/**
 * Created by tmoore on 7/19/14.
 */
public class TracksAdapter extends ArrayAdapter<Track> {

    private final String LOG_TAG = TracksAdapter.class.getSimpleName();

    private String headerText = null;
    private Track track = null;

    public TracksAdapter(Context context, List<Track> tracks) {
        super(context, R.layout.list_item_tracks, tracks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        track = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_tracks, parent, false);
        }
        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.list_item_track_title);
        TextView tvDuration = (TextView) convertView.findViewById(R.id.list_item_track_time);
        // Populate the data into the template view using the data object
        tvTitle.setText(track.getTitle());
        tvDuration.setText(track.getDuration()+"");
//        // Return the completed view to render on screen
        return convertView;
    }



}
