package com.app.player.music.stefan.androidmusicplayer.BackEnd.Music;

import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Stefan on 10-4-2017.
 */

public class Song {
    private String title;
    private String fullPath;
    private String album;
    private String albumArt;
    private String id;

    public Song(String title, String fullPath, String album, String id){
        this.title = title;
        this.fullPath = fullPath;
        this.album = album;
        albumArt = "Not initialised";
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getAlbum() {
        return album;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getId() {
        return id;
    }
}
