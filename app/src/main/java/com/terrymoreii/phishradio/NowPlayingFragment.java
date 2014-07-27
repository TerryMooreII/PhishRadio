package com.terrymoreii.phishradio;

/**
 * Created by tmoore on 7/22/14.
 */

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.terrymoreii.phishradio.model.PlayList;
import com.terrymoreii.phishradio.model.Track;
import com.terrymoreii.phishradio.service.ShowsService;

import java.util.ArrayList;
import java.util.List;


public class NowPlayingFragment extends Fragment {

    private ShowPlayer musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    PlayList playList;
    boolean isPlaying = false;
    private TrackChangeReceiver receiver;
    private Handler mHandler = new Handler();


    //Controls
    TracksAdapter tracksAdapter = null;
    ImageButton playPauseBtn;
    TextView tvSongTitle;
    TextView tvVenue;
    ListView listView;
    SeekBar seekBar;

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


        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        tvSongTitle = (TextView) rootView.findViewById(R.id.textView_song_title);
        tvVenue = (TextView) rootView.findViewById(R.id.textView_venue);
        tvVenue.setText(playList.getShowDetails().getVenue().getName());
        listView = (ListView) rootView.findViewById(R.id.listview_trackslist);

        // tvSongTitle.setText(tvSongTitle.getTitle());

        List<Track> tracks = new ArrayList<Track>();
        Track track = new Track();
        tracksAdapter = new TracksAdapter(getActivity(), tracks);
        playPauseBtn = (ImageButton) rootView.findViewById(R.id.image_play_btn);

        if (tracksAdapter != null) {
            listView.setAdapter(tracksAdapter);
            tracksAdapter.clear();
            tracksAdapter.addAll(playList.getShowDetails().getTracks());
            tracksAdapter.notifyDataSetChanged();
        }

        IntentFilter filter = new IntentFilter(TrackChangeReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new TrackChangeReceiver();
        getActivity().registerReceiver(receiver, filter);

        //Click Listeners
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                musicSrv.setSong(position);
                musicSrv.playSong();
                isPlaying = true;

            }
        });

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

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(musicSrv != null){

                    int currentPosition = musicSrv.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                }
                mHandler.postDelayed(this, 1000);

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
//        We dont want the music to stop on navigation
//        getActivity().stopService(playIntent);
//        musicSrv=null;
        super.onDestroy();
    }

    //connect to the service
    //This allows us to control the service via the view.
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ShowPlayer.MusicBinder binder = (ShowPlayer.MusicBinder)service;
            //get service
            if (!musicBound)
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


    //Recieve event that track has changed
    public class TrackChangeReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "Track";

        @Override
        public void onReceive(Context context, Intent intent) {

            Track currentSong = musicSrv.getCurrentSong();
            tvSongTitle.setText(currentSong.getTitle());
            //seekBar.setProgress(0);
            seekBar.setMax(currentSong.getDuration());
        }

    }
}


