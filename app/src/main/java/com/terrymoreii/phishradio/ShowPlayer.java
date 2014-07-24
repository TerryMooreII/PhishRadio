package com.terrymoreii.phishradio;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.terrymoreii.phishradio.model.ShowDetails;
import com.terrymoreii.phishradio.model.Track;

import java.io.IOException;
import java.util.List;

public class ShowPlayer extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final String LOG_TAG = ShowPlayer.class.getSimpleName();
    private final IBinder musicBind = new MusicBinder();

    private MediaPlayer player;
    //song list
    private List<Track> songs;
    //current position
    private int songPosn;

    private static final String ACTION_PLAY = "com.example.action.PLAY";

    public ShowPlayer() {
    }

    public void onCreate(){

        //Listen for phone state
        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        //Listen for Headphones
        HeadSetReceiver headSetReceiver = new HeadSetReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headSetReceiver, filter);

        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(List<Track> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        ShowPlayer getService() {
            return ShowPlayer.this;
        }
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public void pause(){
        player.pause();
    }

    public void play(){
        player.start();
    }

    public void playSong(){
        player.reset();
        //get song
        Track playSong = songs.get(songPosn);
        //get id
        String url = playSong.getMp3();

        try{
            player.setDataSource(url);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (songPosn < songs.size()){
            songPosn++;
            playSong();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }


    //Listen for phone calls and pause player
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                pause();
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                play();
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                pause();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    //Listen for Headphone plugged in or removed
    public class HeadSetReceiver extends BroadcastReceiver {

        public HeadSetReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        pause();
                        break;
                    case 1:
                        play();
                        break;
                }
            }
        }
    }
}
