package com.app.player.music.stefan.androidmusicplayer;

import android.support.v4.app.Fragment;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.MusicQueuePlayer;

public interface Callback {
    public void onItemClicked(Fragment newFragment);
    public MusicQueuePlayer GetMusicQuePlayer();

}
