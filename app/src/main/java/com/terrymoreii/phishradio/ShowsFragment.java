package com.terrymoreii.phishradio;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.terrymoreii.phishradio.model.Show;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmoore on 7/17/14.
 */
public class ShowsFragment extends Fragment {

    private final String LOG_TAG = ShowsFragment.class.getSimpleName();
    ShowsAdapter showsAdapter = null;

    public ShowsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_shows, container, false);
        String year = "2014";

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
             year = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        FetchShowsTask fetchShowsTask = new FetchShowsTask();
        fetchShowsTask.execute(year);

        List<Show> shows = new ArrayList<Show>();

        showsAdapter = new ShowsAdapter(getActivity(), shows);

        ListView listView = (ListView) rootView.findViewById(R.id.listView_shows);
        listView.setAdapter(showsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Show show = showsAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ShowDetailsActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, show.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchShowsTask extends AsyncTask<String, Void, List<Show> >{
        private final String LOG_TAG = FetchShowsTask.class.getSimpleName();

        @Override
        protected void onPostExecute(List<Show> shows) {
            super.onPostExecute(shows);

            if (shows != null) {
                showsAdapter.clear();
                showsAdapter.addAll(shows);
                showsAdapter.notifyDataSetChanged();
            }else{
                Toast.makeText(showsAdapter.getContext(), "Unable to fetch shows from the service.", Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected List<Show> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String JsonStr = null;
            String year = params[0];

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://phish.in/api/v1/years/" + year + ".json");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(false);
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.connect();

                Log.d(LOG_TAG, "Response Code: " + urlConnection.getResponseCode());

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                JsonStr = buffer.toString();
                Log.d("Terr", JsonStr);
                return parseShows(JsonStr);

            }catch (SocketTimeoutException se){
                Log.e(LOG_TAG, "SocketTimeoutException", se);
                return null;
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                Log.e(LOG_TAG, "IO Exception");

                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


        }
    }

    private List<Show> parseShows(String jsonStr){
        Log.d("Terr", jsonStr);
        List<Show> shows = new ArrayList<Show>();
        JSONObject obj = null;
        try {
            //arr = new JSONArray(jsonStr);
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
}



