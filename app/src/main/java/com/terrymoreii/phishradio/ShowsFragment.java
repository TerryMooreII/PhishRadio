package com.terrymoreii.phishradio;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

import com.terrymoreii.phishradio.model.Show;
import com.terrymoreii.phishradio.service.ShowsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmoore on 7/17/14.
 */
public class ShowsFragment extends Fragment {

    private final String LOG_TAG = ShowsFragment.class.getSimpleName();
    ShowsAdapter showsAdapter = null;
    private ResponseReceiver receiver;
    List<Show> shows = null;

    public ShowsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_shows, container, false);
        String year = "2014";

        Bundle bundle = this.getArguments();
        year = bundle.getString(Intent.EXTRA_TEXT);

        Intent showsService = new Intent(getActivity(), ShowsService.class)
                .putExtra(ShowsService.YEAR, year);
        getActivity().startService(showsService);

        //Receiver for the broadcast.
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        getActivity().registerReceiver(receiver, filter);

        List<Show> shows = new ArrayList<Show>();
        ListView listView = (ListView) rootView.findViewById(R.id.listView_shows);
        showsAdapter = new ShowsAdapter(getActivity(), shows);
        listView.setAdapter(showsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Show show = showsAdapter.getItem(position);

                // Create new fragment and transaction
                Bundle bundle = new Bundle();
                bundle.putInt(Intent.EXTRA_TEXT, show.getId());

                Fragment newFragment = new ShowDetailsFragment();
                newFragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, newFragment)
                        .addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }

    private List<Show> parseShows(String jsonStr){
        List<Show> shows = new ArrayList<Show>();
        JSONObject obj = null;
        try {
            obj = new JSONObject(jsonStr);

            JSONArray data = obj.getJSONArray("data");

            for (int i=0; i < data.length(); i++) {
                shows.add(convertShow(data.getJSONObject(i)));
            }

        }catch(JSONException e){
            Log.d(LOG_TAG, "Unable to parse show json", e);
        }
        return shows;
    }

    private Show convertShow(JSONObject obj) throws JSONException{
        int id = obj.getInt("id");
        String date = obj.getString("date");
        String venueName = obj.getString("venue_name");
        String location = obj.getString("location");

        return new Show(id, date, venueName, location);

    }

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "Shows_Result";

        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra(ShowsService.SHOWS_RESULT);

            shows = parseShows(json);
            if (shows != null) {

                if (shows != null) {
                    showsAdapter.clear();
                    showsAdapter.addAll(shows);
                    showsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(showsAdapter.getContext(), "No show.", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(showsAdapter.getContext(), "Unable to fetch shows from the service.", Toast.LENGTH_LONG).show();
            }

        }
    }
}



