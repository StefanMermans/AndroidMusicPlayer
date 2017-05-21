package com.app.player.music.stefan.androidmusicplayer.BackEnd;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.PlayingVisualiser;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Song;
import com.app.player.music.stefan.androidmusicplayer.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Stefan on 3-5-2017.
 */
public class MusicQueuePlayer extends MediaPlayer{
    private Song[] songQueue;
    private int currentSongIndex;
    private OnSeekChangeListener onSeekChangeListener;
    private SeekBar seekBar;
    private Thread seekThread;
    private PlayingVisualiser playingVisualiser;
    private StickyNotification stickyNotification;

    private class SeekUpdater implements Runnable{

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            while (true){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                if(!isPlaying())
                {
                    continue;
                }

                seekBar.incrementProgressBy(getCurrentPosition() - seekBar.getProgress());
            }
        }
    }

    private class OnSeekChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean userTouch) {
            if(isPlaying() && userTouch){
                seekTo(seekBar.getProgress());
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    public MusicQueuePlayer(Context context){
        songQueue = new Song[] {}; // Init array empty
        currentSongIndex = 0;
        onSeekChangeListener = new OnSeekChangeListener();
        seekThread = new Thread();
        stickyNotification = new StickyNotification(context, this);

        setOnCompletionListener(new OnComplete());
    }

    @Override
    public void reset(){
        songQueue = new Song[] {}; // Init array empty
        currentSongIndex = 0;

        super.reset();
    }

    public boolean Begin(String id, ArrayList<Song> songQueueList){
        this.songQueue = songQueueList.toArray(new Song[songQueueList.size()]);

        // check if index is valid
        int index = GetSongIndex(id);
        if(index == -1) return false;

        currentSongIndex = index;

        stop();
        super.reset();

        try{


            setDataSource(songQueue[currentSongIndex].getFullPath());
            prepare();
            start();

            stickyNotification.PushNotification(songQueue[currentSongIndex].getTitle(),songQueue[currentSongIndex].getAlbumArt(),true);
            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public void RegisterSeekBar(SeekBar seekBar){
        // Set every needed seekBar value
        seekBar.setMax(getDuration());
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(onSeekChangeListener);
        this.seekBar = seekBar;

        // Start the seekBar updater;
        if(seekThread.isAlive()){
            seekThread.interrupt();
        }

        SeekUpdater seekUpdater = new SeekUpdater();
        seekThread = new Thread(seekUpdater);
        seekThread.start();
    }

    public boolean Next(){
        if(songQueue.length <= 0) return false;

        try{
            stop();
            super.reset();

            if(songQueue.length > (currentSongIndex + 1)){
                currentSongIndex += 1;
            } else{
                currentSongIndex = 0;
            }

            setDataSource(songQueue[currentSongIndex].getFullPath());

            prepare();
            start();

            if(playingVisualiser != null){
                playingVisualiser.Next();
            }

            stickyNotification.PushNotification(songQueue[currentSongIndex].getTitle(),songQueue[currentSongIndex].getAlbumArt(),true);

            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }

    }

    public boolean Prev(){
        if(songQueue.length <= 0) return false;

        try{
            stop();
            super.reset();

            if((currentSongIndex - 1) >= 0){
                currentSongIndex -= 1;
            } else{
                currentSongIndex = songQueue.length -1;
            }

            setDataSource(songQueue[currentSongIndex].getFullPath());

            prepare();
            start();

            if(playingVisualiser != null){
                playingVisualiser.Prev();
            }

            stickyNotification.PushNotification(songQueue[currentSongIndex].getTitle(),songQueue[currentSongIndex].getAlbumArt(),true);

            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }


    public int GetSongIndex(String songId){

        for(int i = 0; i < songQueue.length; i++){
            if(songQueue[i].getId().equals(songId)){
                return i;
            }
        }

        return -1;
    }

    public PlayingVisualiser getPlayingVisualiser() {
        return playingVisualiser;
    }

    public void setPlayingVisualiser(PlayingVisualiser playingVisualiser) {
        this.playingVisualiser = playingVisualiser;
    }

    public Song[] GetSongQueue(){
        return songQueue;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public OnSeekChangeListener getOnSeekChangeListener() {
        return onSeekChangeListener;
    }

    @Override
    public void stop() throws IllegalStateException {
        stickyNotification.Stop();

        super.stop();
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();

        stickyNotification.PushNotification(
                songQueue[currentSongIndex].getTitle(),
                songQueue[currentSongIndex].getAlbumArt(),
                true);
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();

        stickyNotification.PushNotification(
                songQueue[currentSongIndex].getTitle(),
                songQueue[currentSongIndex].getAlbumArt(),
                false);
    }

    private class OnComplete implements OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Next();
        }
    }
}