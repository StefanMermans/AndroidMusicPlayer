package com.app.player.music.stefan.androidmusicplayer.BackEnd.Music;

import java.util.ArrayList;

/**
 * Created by Stefan on 12-4-2017.
 */

public class Album {
    private String name;

    private String albumArt;
    private String id;

    public Album(String name, String albumArt, String id){
        this.name = name;
        this.albumArt = albumArt;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getId() {
        return id;
    }
}
