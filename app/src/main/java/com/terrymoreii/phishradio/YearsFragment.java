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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.widget.AdapterView.*;

/**
 * Created by tmoore on 7/17/14.
 */
public class YearsFragment extends Fragment {

    public YearsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);

        List<String> years = new ArrayList<String>();
        for (int i = 2014; i > 1982; i--){
            years.add(i + "");
        }
        final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_years, R.id.list_item_years_textview, years);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_years);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String year = adapter.getItem(position).toString();
                //Toast.makeText(getActivity(), year, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ShowsActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, year);
                startActivity(intent);
            }
        });


        return rootView;
    }




}