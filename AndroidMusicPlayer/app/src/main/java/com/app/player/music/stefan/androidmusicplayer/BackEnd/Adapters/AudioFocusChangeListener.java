package com.app.player.music.stefan.androidmusicplayer.BackEnd.Adapters;

import android.media.AudioManager;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.MusicQueuePlayer;
import com.app.player.music.stefan.androidmusicplayer.Callback;

/**
 * Created by Stefan on 1-5-2017.
 */

public class AudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
    private Callback callback;

    public AudioFocusChangeListener(Callback callback){
        this.callback = callback;
    }

    @Override
    public void onAudioFocusChange(int i) {
        MusicQueuePlayer musicQueuePlayer = callback.GetMusicQuePlayer();

        // TODO improve upon this
        // Maybe integrate this function in the musicQueuePlayer
        switch (i) {
            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                musicQueuePlayer.setVolume(0.2f, 0.2f);
                break;
            case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                if(musicQueuePlayer.isPlaying()){
                    musicQueuePlayer.stop(); // TODO: pause?
                }
                musicQueuePlayer.reset();
                break;
            case (AudioManager.AUDIOFOCUS_LOSS):
                if(musicQueuePlayer.isPlaying()){
                    musicQueuePlayer.stop();
                }
                musicQueuePlayer.reset();
                break;
            case (AudioManager.AUDIOFOCUS_GAIN):
                // Return the volume to normal and resume if paused.
                musicQueuePlayer.setVolume(1f, 1f);
                musicQueuePlayer.start();
                break;
        }
    }
}
