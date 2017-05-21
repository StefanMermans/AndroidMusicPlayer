package com.app.player.music.stefan.androidmusicplayer.BackEnd.Music;

import java.util.Comparator;

/**
 * Created by Stefan on 3-5-2017.
 */

public class SongComparator implements Comparator<Song>{
    @Override
    public int compare(Song song, Song t1) {
        return song.getTitle().compareTo(t1.getTitle());
    }
}
