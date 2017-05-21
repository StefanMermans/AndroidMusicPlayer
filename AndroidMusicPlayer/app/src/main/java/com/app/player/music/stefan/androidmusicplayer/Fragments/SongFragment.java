package com.app.player.music.stefan.androidmusicplayer.Fragments;


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
import android.widget.ListView;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Adapters.SongAdapter;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Song;
import com.app.player.music.stefan.androidmusicplayer.R;
import com.app.player.music.stefan.androidmusicplayer.Callback;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Stefan on 23-4-2017.
 */
class SongClickAdapter extends FragmentActivity implements  AdapterView.OnItemClickListener{
    private Context context;
    private ArrayList<Song> songs;
    private Callback callback;
    private String albumFilter;

    public SongClickAdapter(Context context, ArrayList<Song> songs, Callback callback, String albumFilter) {
        this.context = context;
        this.songs = songs;
        this.callback = callback;
        this.albumFilter = albumFilter;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Song song = (Song) adapterView.getItemAtPosition(i);

//        MusicQueuePlayer musicQuePlayer= callback.GetMusicQuePlayer();
//        musicQuePlayer.Begin(i, songs);

        Bundle args = new Bundle();
        args.putString(PlayingFragment.ARG_ALBUM_FILTER, albumFilter);
        args.putString(PlayingFragment.ARG_SONG_INDEX_FILTER, song.getId());

        PlayingFragment playingFragment = new PlayingFragment();
        playingFragment.setArguments(args);

        callback.GetMusicQuePlayer().Begin(song.getId(),songs);
        callback.onItemClicked(playingFragment);
    }
}

public class SongFragment extends Fragment {
    public static String ARG_ALBUM_FILTER = "alFil";

    private Callback callback;
    private String albumFilter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        ListView listView = (ListView) inflater.inflate(R.layout.frag_song, container, false);


        return listView;
    }

    @Override
    public void onStart() {
        super.onStart();

        final Toolbar toolbar = (Toolbar)this.getActivity().findViewById(R.id.toolbar);
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Songs");
            }
        });

        Bundle args = getArguments();
        if(args != null){
            albumFilter = args.getString(ARG_ALBUM_FILTER);
        }

        DisplaySongs((ListView)this.getView());
    }

    void DisplaySongs(ListView songsView){
        ArrayList<Song> songs = new ArrayList<>();

        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String filter = null;
        if(albumFilter != null){
            filter = MediaStore.Audio.Media.ALBUM + "='" + albumFilter + "'";
        }

        Cursor cursor = this.getActivity().getContentResolver().query(
            allSongsUri,
            new String[]{
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID
            },
            filter,
            null,
            MediaStore.Audio.Media.TITLE
        );

        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    songs.add(new Song(
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    ));
                } while (cursor.moveToNext());
                cursor.close();
            }
        }

        if(songs.size() > 0) {
            Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            Cursor albumCursor = this.getActivity().getContentResolver().query(
                    albumUri,
                    new String[]{
                            MediaStore.Audio.Albums._ID,
                            MediaStore.Audio.Albums.ALBUM_ART,
                            MediaStore.Audio.Albums.ALBUM
                    },
                    MediaStore.Audio.Albums.ALBUM + "='" + songs.get(0).getAlbum() + "'",
                    null,
                    MediaStore.Audio.Albums.ALBUM
            );

            String albumArt;
            if (albumCursor != null) {
                if (albumCursor.moveToFirst()) {
                    albumArt = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                    for (Song song : songs) {
                        song.setAlbumArt(albumArt);
                    }
                }
                albumCursor.close();
            }
        }

        songsView.setAdapter(new SongAdapter(this.getActivity(),songs));
        songsView.setOnItemClickListener(new SongClickAdapter(this.getContext(),songs,callback, albumFilter));
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
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
