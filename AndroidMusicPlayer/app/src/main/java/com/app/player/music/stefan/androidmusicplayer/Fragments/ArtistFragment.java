package com.app.player.music.stefan.androidmusicplayer.Fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Adapters.ArtistAdapter;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Artist;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Song;
import com.app.player.music.stefan.androidmusicplayer.R;
import com.app.player.music.stefan.androidmusicplayer.Callback;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Stefan on 23-4-2017.
 */

class ArtistClickAdapter extends FragmentActivity implements  AdapterView.OnItemClickListener{
    private Context context;
    private ArrayList<Artist> artists;
    private Callback callback;

    public ArtistClickAdapter(Context context, ArrayList<Artist> artists, Callback callback) {
        this.context = context;
        this.artists = artists;
        this.callback = callback;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlbumFragment albumFragment = new AlbumFragment();

        Bundle args = new Bundle();
        args.putString(AlbumFragment.ARG_ARTIST_FILTER, artists.get(i).getName());
        albumFragment.setArguments(args);

        callback.onItemClicked(albumFragment);
    }
}

public class ArtistFragment extends Fragment{
    private Callback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        GridView albumView = (GridView) inflater.inflate(R.layout.album_container, container, false);
        return albumView;
//        albumView.setAdapter(new AlbumAdapter(this, ));
//        albumView.setOnItemClickListener(new Album);

    }

    public void DisplayArtists(GridView albumView) {
        ArrayList<Artist> artists = new ArrayList<>();

        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

        Cursor cursor = this.getActivity().getContentResolver().query(
            artistUri,
            new String[]{
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
            },
            null,
            null,
            MediaStore.Audio.Artists.ARTIST
        );

        if (cursor != null){
            if(cursor.moveToFirst()){
                do{
                    artists.add(new Artist(
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)),
                        null,
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))  ));
                } while (cursor.moveToNext());
            }
        }

       for(Artist artist : artists) {
            Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            Cursor albumCursor = this.getActivity().getContentResolver().query(
                    albumUri,
                    new String[]{
                            MediaStore.Audio.Albums._ID,
                            MediaStore.Audio.Albums.ALBUM_ART,
                            MediaStore.Audio.Albums.ALBUM
                    },
                    MediaStore.Audio.Albums.ARTIST + "='" + artist.getName() + "'",
                    null,
                    MediaStore.Audio.Albums.ALBUM
            );

            String albumArt;
            if (albumCursor != null) {
                if (albumCursor.moveToFirst()) {
                    albumArt = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                    artist.setAlbumArt(albumArt);
                }
                albumCursor.close();
            }
        }

        albumView.setAdapter(new ArtistAdapter(this.getActivity(), artists));
        albumView.setOnItemClickListener(new ArtistClickAdapter(this.getContext(),artists, callback));
    }

    @Override
    public void onStart() {
        super.onStart();

        Activity activity = this.getActivity();

        final Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Artists");
            }
        });

        DisplayArtists((GridView)this.getView());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = android.support.v4.app.Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            callback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
