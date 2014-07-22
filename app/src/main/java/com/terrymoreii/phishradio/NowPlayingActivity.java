package com.terrymoreii.phishradio;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;
import android.widget.TextView;

import com.terrymoreii.phishradio.R;
import com.terrymoreii.phishradio.model.PlayList;
import com.terrymoreii.phishradio.model.ShowDetails;
import com.terrymoreii.phishradio.model.Track;

import java.util.ArrayList;
import java.util.List;

public class NowPlayingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.now_playing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);
            PlayList playList = (PlayList)getActivity().getIntent()
                    .getSerializableExtra("PlayList");

            Log.d("XXX", playList.getShowDetails().getVenue().getName());

//
            TextView tvSongTitle = (TextView) rootView.findViewById(R.id.textView_song_title);
            TextView tvVenue = (TextView) rootView.findViewById(R.id.textView_venue);


            Track tracks1 = (Track) playList.getShowDetails().getTracks().get(playList.getCurrentPosition());

            tvSongTitle.setText(tracks1.getTitle());
            tvVenue.setText(playList.getShowDetails().getVenue().getName());

            ListView listView = (ListView) rootView.findViewById(R.id.listview_trackslist);
            TracksAdapter tracksAdapter = null;

            List<Track> tracks = new ArrayList<Track>();
            Track track = new Track();
            track.setTitle("");
            track.setDuration(0);
            tracks.add(track);


            tracksAdapter = new TracksAdapter(getActivity(), tracks);

            if (tracksAdapter != null) {
                listView.setAdapter(tracksAdapter);
                tracksAdapter.clear();
                tracksAdapter.addAll(playList.getShowDetails().getTracks());
                tracksAdapter.notifyDataSetChanged();
            }

            return rootView;
        }
    }
}
