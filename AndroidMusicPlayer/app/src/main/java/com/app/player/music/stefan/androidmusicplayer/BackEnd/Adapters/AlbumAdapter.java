package com.app.player.music.stefan.androidmusicplayer.BackEnd.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Album;
import com.app.player.music.stefan.androidmusicplayer.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Stefan on 12-4-2017.
 */

public class AlbumAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Album> albums;

    public AlbumAdapter(Context context, ArrayList<Album> albums){
        this.context = context;
        this.albums = albums;
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int i) {
        return albums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View albumView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null){
            albumView = new View(context);
            albumView = inflater.inflate(R.layout.album_view, null);
        } else{
            albumView = (View) view;
        }

        TextView textView = (TextView) albumView.findViewById(R.id.AlbumNameView);
        ImageView imageView = (ImageView) albumView.findViewById(R.id.AlbumImageView);

        textView.setText(albums.get(i).getName());

        if(albums.get(i).getAlbumArt() == null){
            imageView.setImageResource(R.drawable.music_note);
        } else{
            File imageFile = new File(albums.get(i).getAlbumArt()); // TODO Is using a File necessary?
            Bitmap albumImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(albumImage);
        }

        return albumView;
    }
}
