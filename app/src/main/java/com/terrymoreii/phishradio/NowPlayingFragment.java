package com.terrymoreii.phishradio;

/**
 * Created by tmoore on 7/22/14.
 */

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.terrymoreii.phishradio.model.PlayList;
import com.terrymoreii.phishradio.model.Track;

import java.util.ArrayList;
import java.util.List;


public class NowPlayingFragment extends Fragment {

    private ShowPlayer musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    PlayList playList;
    TracksAdapter tracksAdapter = null;

    ImageButton playPauseBtn;
    boolean isPlaying = false;

    public NowPlayingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        playList = (PlayList)getActivity().getIntent()
                .getSerializableExtra("PlayList");

        if(playIntent==null){
            playIntent = new Intent(getActivity(), ShowPlayer.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }

        TextView tvSongTitle = (TextView) rootView.findViewById(R.id.textView_song_title);
        TextView tvVenue = (TextView) rootView.findViewById(R.id.textView_venue);

        // tvSongTitle.setText(tvSongTitle.getTitle());
        tvVenue.setText(playList.getShowDetails().getVenue().getName());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_trackslist);


        List<Track> tracks = new ArrayList<Track>();
        Track track = new Track();
        tracksAdapter = new TracksAdapter(getActivity(), tracks);

        if (tracksAdapter != null) {
            listView.setAdapter(tracksAdapter);
            tracksAdapter.clear();
            tracksAdapter.addAll(playList.getShowDetails().getTracks());
            tracksAdapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                musicSrv.setSong(position);
                musicSrv.playSong();
                isPlaying = true;

            }
        });

        playPauseBtn = (ImageButton) rootView.findViewById(R.id.image_play_btn);

        playPauseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isPlaying){
                    pause();
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                }else{
                    play();
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                }
                isPlaying = !isPlaying;
            }

        });


        return rootView;
    }

    public void play(){
        musicSrv.play();
    }

    public void pause(){
        musicSrv.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
         getActivity().stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ShowPlayer.MusicBinder binder = (ShowPlayer.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(playList.getShowDetails().getTracks());
            musicBound = true;
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
}


