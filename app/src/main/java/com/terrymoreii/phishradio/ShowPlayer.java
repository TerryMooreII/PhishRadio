package com.terrymoreii.phishradio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.terrymoreii.phishradio.model.PlayList;
import com.terrymoreii.phishradio.model.Track;

import java.util.List;

public class ShowPlayer extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private static final int NOTIFY_ID=1;
    private final String LOG_TAG = ShowPlayer.class.getSimpleName();
    private final IBinder musicBind = new MusicBinder();
    private Track currentSong;

    private MediaPlayer player;
    //song list
    private List<Track> songs;
    //current position
    private int songPosn;
    private boolean isPaused;
    private int bufferPercentage;

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

        //This show the current track being played in
        //the notification tray.
        // showNotification();
    }

    private void showNotification(){
        Intent notIntent = new Intent(this, MyActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        Track currentSong = songs.get(songPosn);

        builder.setContentIntent(pendInt)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setTicker(currentSong.getTitle())
                .setOngoing(true)
                .setContentTitle("Playing...")
        .setContentText(currentSong.getTitle());
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public void pause(){

        player.pause();
        isPaused = true;
    }

    public void play(){

        player.start();
        isPaused = false;
    }

    public void playSong(){
        if (player == null)
            Log.d(LOG_TAG, "player is null");
        else
            Log.d(LOG_TAG, "player is not null");

        player.reset();
        //get song
        currentSong = songs.get(songPosn);
        //get id
        String url = currentSong.getMp3();

        try{
            player.setDataSource(url);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();

//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction(NowPlayingFragment.TrackChangeReceiver.ACTION_RESP);
//        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//        sendBroadcast(broadcastIntent);
    }

    public Track getCurrentSong(){
        return this.currentSong;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
//        player.stop();
//        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(songs == null) {
            //player.release();
            return;
        }

        if (songPosn < songs.size()){
            songPosn++;
            playSong();
        }else{
            player.stop();
            player.release();
        }
    }

    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public boolean isPaused(){
        return isPaused;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setBufferPercentage(percent * currentSong.getDuration() / 100);
    }

    public int getBufferPercentage() {
        return bufferPercentage;
    }

    public void setBufferPercentage(int bufferPercentage) {
        this.bufferPercentage = bufferPercentage;
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
