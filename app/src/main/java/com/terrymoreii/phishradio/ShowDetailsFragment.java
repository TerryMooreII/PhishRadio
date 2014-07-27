package com.terrymoreii.phishradio;

/**
 * Created by tmoore on 7/19/14.
 */

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.terrymoreii.phishradio.model.PlayList;
import com.terrymoreii.phishradio.model.ShowDetails;
import com.terrymoreii.phishradio.model.Track;
import com.terrymoreii.phishradio.model.Venue;
import com.terrymoreii.phishradio.service.ShowDetailsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowDetailsFragment extends Fragment {

    private final String LOG_TAG = ShowDetailsFragment.class.getSimpleName();

    TracksAdapter tracksAdapter = null;
    ShowDetails showDetails = null;
    ShowPlayer showPlayer = new ShowPlayer();
    private ResponseReceiver receiver;

    public ShowDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_details, container, false);

        Intent intent = getActivity().getIntent();
        int id = 0;

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            id = intent.getIntExtra(Intent.EXTRA_TEXT, 3);
        }

        Intent showDetailsService = new Intent(getActivity(), ShowDetailsService.class)
                .putExtra(ShowDetailsService.SHOW_ID, id + "");
        getActivity().startService(showDetailsService);

        List<Track> tracks = new ArrayList<Track>();
        Track track = new Track();

        tracksAdapter = new TracksAdapter(getActivity(), tracks);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_trackslist);

        if (tracksAdapter != null)
            listView.setAdapter(tracksAdapter);

        //Receiver for the broadcast.
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        getActivity().registerReceiver(receiver, filter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Track track = tracksAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), NowPlayingActivity.class);
                Bundle mBundle = new Bundle();
                PlayList playList = new PlayList();
                playList.setShowDetails(showDetails);
                playList.setCurrentPosition(position);
                mBundle.putSerializable("PlayList",playList);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    private ShowDetails parse(String jsonStr) {


            JSONObject obj = null;
            try {
                //arr = new JSONArray(jsonStr);
                obj = new JSONObject(jsonStr);

                JSONObject data = obj.getJSONObject("data");

                showDetails = getShowDetails(data);

                Venue venue = getVenue(data.getJSONObject("venue"));

                List<Track> tracks = getTracks(data.getJSONArray("tracks"));

                if (venue != null){
                    showDetails.setVenue(venue);
                }

                if (tracks != null){
                    showDetails.setTracks(tracks);
                }

            } catch (JSONException e) {
                Log.d(LOG_TAG, "Unable to parse show json", e);
            }
            return showDetails;
        }

        private ShowDetails getShowDetails (JSONObject obj) throws JSONException {

            ShowDetails showDetails = new ShowDetails();

            showDetails.setShowId(obj.getInt("id"));
            showDetails.setDate(obj.getString("date"));
            showDetails.setDuration(obj.getInt("duration"));
            showDetails.setIncomplete(obj.getBoolean("incomplete"));
            showDetails.setRemastered(obj.getBoolean("remastered"));
            showDetails.setSoundBoard(obj.getBoolean("sbd"));
            showDetails.setTourId(obj.getInt("tour_id"));

            return showDetails;
        }

        private Venue getVenue (JSONObject obj) throws JSONException {
            Venue venue = new Venue();

            venue.setVenueId(obj.getInt("id"));
            venue.setLatitude(obj.getLong("latitude"));
            venue.setLongitude(obj.getLong("longitude"));
            venue.setLocation(obj.getString("location"));
            venue.setName(obj.getString("name"));
            venue.setShowCount(obj.getInt("shows_count"));
            venue.setSlug(obj.getString("slug"));

            return venue;
        }


        private List<Track> getTracks (JSONArray array) throws JSONException {

            List<Track> tracks = new ArrayList<Track>();
            for (int i=0; i<array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                Track track = new Track();
                track.setSongId(obj.getInt("id"));
                track.setSlug(obj.getString("slug"));
                track.setLikeCount(obj.getInt("likes_count"));
                track.setPosition(obj.getInt("position"));
                track.setMp3(obj.getString("mp3"));
                track.setDuration(obj.getInt("duration"));
                track.setTitle(obj.getString("title"));
                track.setSet(obj.getString("set"));
                track.setSetName(obj.getString("set_name"));

                tracks.add(track);
            }

            return tracks;
        }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "SHOW_DETAILS_RESULT";

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra(ShowDetailsService.SHOW_DETAILS_RESULT);

            showDetails = parse(json);
            if (showDetails != null) {
                TextView tvVenue = (TextView) getActivity().findViewById(R.id.textview_venue);
                TextView tvLocation = (TextView) getActivity().findViewById(R.id.textview_location);

                tvVenue.setText(showDetails.getVenue().getName());
                tvLocation.setText(showDetails.getVenue().getLocation());

                List<Track> tracks = showDetails.getTracks();
                if (tracks != null){
                    tracksAdapter.clear();
                    tracksAdapter.addAll(tracks);
                    tracksAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(tracksAdapter.getContext(), "No tracks.", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(tracksAdapter.getContext(), "Unable to fetch shows from the service.", Toast.LENGTH_LONG).show();
            }

        }
    }

}