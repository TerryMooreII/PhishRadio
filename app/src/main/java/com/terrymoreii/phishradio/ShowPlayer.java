package com.terrymoreii.phishradio;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.terrymoreii.phishradio.model.ShowDetails;

public class ShowPlayer extends Service {

    private final String LOG_TAG = ShowPlayer.class.getSimpleName();
    MediaPlayer mediaPlayer = null;

    ShowDetails showDetails = null;

    public ShowPlayer() {
    }


    public void playAudio(String url) throws Exception
    {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    public void stop(){
        mediaPlayer.stop();
    }

    private void killMediaPlayer() {
        if(mediaPlayer!=null) {
            try {
                stop();
                mediaPlayer.release();
            }
            catch(Exception e) {
                Log.d(LOG_TAG, "Error killing media!", e);
            }
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
