package com.terrymoreii.phishradio;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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

                // Create new fragment and transaction
                Bundle bundle = new Bundle();
                bundle.putString(Intent.EXTRA_TEXT, year);

                Fragment newFragment = new ShowsFragment();
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




}