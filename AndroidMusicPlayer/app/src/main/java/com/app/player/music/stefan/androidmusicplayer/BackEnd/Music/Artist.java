package com.app.player.music.stefan.androidmusicplayer.BackEnd.Music;

/**
 * Created by Stefan on 29-4-2017.
 */

public class Artist {
    private String name;
    private String albumArt;
    private String id;

    public Artist(String name, String albumArt, String id) {
        this.name = name;
        this.albumArt = albumArt;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public void setId(String id) {
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
