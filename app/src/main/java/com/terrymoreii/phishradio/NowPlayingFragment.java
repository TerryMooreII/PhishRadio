package com.terrymoreii.phishradio;

/**
 * Created by tmoore on 7/22/14.
 */

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.terrymoreii.phishradio.Utils.TimeUtils;
import com.terrymoreii.phishradio.model.PlayList;
import com.terrymoreii.phishradio.model.Track;

import java.util.ArrayList;
import java.util.List;


public class NowPlayingFragment extends Fragment {

    private final String LOG_TAG = NowPlayingFragment.class.getSimpleName();

    PlayList playList;
    boolean isPlaying = false;
    // private TrackChangeReceiver receiver;
    private Handler mHandler = new Handler();
    private boolean musicThreadFinished = false;
    Thread uiThread;

    //Controls
    TracksAdapter tracksAdapter = null;
    ImageButton playPauseBtn;
    TextView tvSongTitle;
    TextView tvVenue;
    ListView listView;
    TextView musicDuration;
    TextView musicCurLoc;
    SeekBar seekBar;
    private ShowPlayer musicSrv;

    public NowPlayingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        Bundle bundle =  this.getArguments();
         if(savedInstanceState != null){
            playList = (PlayList) savedInstanceState.getSerializable("PlayList");
            Log.d(LOG_TAG, "RESTRING " + playList.getCurrentPosition());
            uiThread = null;
             return rootView;

         }else if (bundle != null) {
            playList = (PlayList) bundle.getSerializable("PlayList");
            Log.d(LOG_TAG, "FROM BUNDLE");

        }else{


            Log.d(LOG_TAG, "NOTHING RESTORED");
        }


        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        tvSongTitle = (TextView) rootView.findViewById(R.id.textView_song_title);
        tvVenue = (TextView) rootView.findViewById(R.id.textView_venue);
        listView = (ListView) rootView.findViewById(R.id.listview_trackslist);
        musicDuration = (TextView) rootView.findViewById(R.id.textView_duration_left);
        musicCurLoc = (TextView) rootView.findViewById(R.id.textView_duration);


        if (playList == null)
            return rootView;

        tvVenue.setText(playList.getShowDetails().getVenue().getName());

        musicSrv = ((HomeActivity) getActivity()).getMusicSrv();
        musicSrv.setList(playList.getShowDetails().getTracks());
        int pos = playList.getCurrentPosition();
        musicSrv.setSong(pos);
        musicSrv.playSong();

        tvSongTitle.setText(musicSrv.getCurrentSong().getTitle());


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

//        IntentFilter filter = new IntentFilter(TrackChangeReceiver.ACTION_RESP);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);
//        receiver = new TrackChangeReceiver();
//        getActivity().registerReceiver(receiver, filter);

        //Click Listeners
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                if (isPlaying) {
                    pause();
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    play();
                    playPauseBtn.setImageResource(android.R.drawable.ic_media_play);
                }
                isPlaying = !isPlaying;
            }

        });

        uiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int currentPosition = 0;
                while (!musicThreadFinished) {
                    try {
                        Thread.sleep(1000);
                        currentPosition = musicSrv.getCurrentPosition();
                    } catch (InterruptedException e) {
                        return;
                    } catch (Exception e) {
                        return;
                    }
                    final int total = musicSrv.getCurrentSong().getDuration();
                    final String totalTime = TimeUtils.getTime(total);
                    final String curTime = TimeUtils.getTime(currentPosition);

                    seekBar.setMax(total); //song duration
                    seekBar.setProgress(currentPosition);  //for current song progress
                    seekBar.setSecondaryProgress(musicSrv.getBufferPercentage());   // for buffer progress
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicDuration.setText(totalTime);
                            musicCurLoc.setText(curTime);
                        }
                    });
                }
            }
        });
        uiThread.start();

        return rootView;
    }

    public void play() {
        musicSrv.play();
    }

    public void pause() {
        musicSrv.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        We dont want the music to stop on navigation
//        getActivity().stopService(playIntent);
//        musicSrv=null;
        super.onDestroy();
        musicThreadFinished = true;
        uiThread = null;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "Saving state");
        if (playList != null)
            outState.putSerializable("PlayList", playList);

    }

    //Recieve event that track has changed
//    public class TrackChangeReceiver extends BroadcastReceiver {
//        public static final String ACTION_RESP =
//                "Track";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            Track currentSong = musicSrv.getCurrentSong();
//            tvSongTitle.setText(currentSong.getTitle());
//
//        }
//
//    }
}


