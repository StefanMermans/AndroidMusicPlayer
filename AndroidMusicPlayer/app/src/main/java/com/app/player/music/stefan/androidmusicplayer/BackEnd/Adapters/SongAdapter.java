package com.app.player.music.stefan.androidmusicplayer.BackEnd.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Song;
import com.app.player.music.stefan.androidmusicplayer.R;

import java.util.ArrayList;

/**
 * Created by Stefan on 30-4-2017.
 */

public class SongAdapter extends BaseAdapter{
    private ArrayList<Song> songs;
    private Context context;

    public SongAdapter(Context context, ArrayList<Song> songs){
        this.context = context;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return songs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView songView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if(view == null){
            songView = new TextView(context);
            songView = (TextView)inflater.inflate(R.layout.song_item, null);
        } else{
            songView = (TextView)view;
        }

        songView.setText(songs.get(i).getTitle().toCharArray(),0,songs.get(i).getTitle().length());

        return songView;
    }
}

