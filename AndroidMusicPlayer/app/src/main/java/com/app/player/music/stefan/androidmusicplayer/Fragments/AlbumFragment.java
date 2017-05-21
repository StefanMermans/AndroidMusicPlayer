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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Adapters.AlbumAdapter;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Album;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Song;
import com.app.player.music.stefan.androidmusicplayer.R;
import com.app.player.music.stefan.androidmusicplayer.Callback;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Stefan on 23-4-2017.
 */

class AlbumClickAdapter extends FragmentActivity implements AdapterView.OnItemClickListener{
    private Context context;
    private ArrayList<Album> albums;
    private Callback callback;

    public AlbumClickAdapter(Context context, ArrayList<Album> albums, Callback callback){
        this.context = context;
        this.albums = albums;
        this.callback = callback;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ArrayList<Song> songs = new ArrayList<>();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = context.getContentResolver().query(
                songUri,
                new String[]{
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media._ID
                },
                MediaStore.Audio.Media.ALBUM_ID + "=" + albums.get(i).getId(),
                null/*new String[]{
                MediaStore.Audio.Media.ALBUM_ID,
                "" + albums.get(i).getId()
            }*/,
                MediaStore.Audio.Media.TITLE
        );

        int index = 0;
        if (songCursor != null) {
            if (songCursor.moveToFirst()) {
                do {
                    songs.add(new Song(
                            songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                            songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                            songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                            songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    ));
                    index++;
                } while (songCursor.moveToNext());

            }
            songCursor.close();
        }

        Bundle args = new Bundle();
        args.putString(SongFragment.ARG_ALBUM_FILTER, albums.get(i).getName());

        SongFragment songFragment = new SongFragment();
        songFragment.setArguments(args);

        callback.onItemClicked(songFragment);
    }
}


public class AlbumFragment extends Fragment {
    public static String ARG_ARTIST_FILTER = "artFil";

    private Callback callback;
    private String artistFilter;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        GridView albumView = (GridView) inflater.inflate(R.layout.album_container, container, false);
//        DisplayAlbums(albumView);
        return albumView;
//        albumView.setAdapter(new AlbumAdapter(this, ));
//        albumView.setOnItemClickListener(new Album);

    }

    public void DisplayAlbums(GridView albumView) {
        ArrayList<Album> albums = new ArrayList<>();

        Uri allAlbumsUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        if(artistFilter != null){
            artistFilter = MediaStore.Audio.Albums.ARTIST + "='" + artistFilter + "'";
        }

        Cursor cursor = this.getActivity().getContentResolver().query(
                allAlbumsUri,
                new String[]{
                        MediaStore.Audio.Albums.ALBUM_ART,
                        MediaStore.Audio.Albums.ALBUM,
                        MediaStore.Audio.Albums._ID,
                        MediaStore.Audio.Albums.ARTIST},
                artistFilter,
                null,
                MediaStore.Audio.Albums.ALBUM
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // TODO: Less ugly
                    albums.add(new Album(
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))));
                } while (cursor.moveToNext());

            }
            cursor.close();

        }

        albumView.setAdapter(new AlbumAdapter(this.getActivity() ,albums));
        albumView.setOnItemClickListener(new AlbumClickAdapter(this.getContext(), albums, callback));
    }

    @Override
    public void onStart() {
        super.onStart();

        Activity activity = this.getActivity();

        final Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Album");
            }
        });

        Bundle args = getArguments();
        if(args != null){
            artistFilter = args.getString(ARG_ARTIST_FILTER);
        }

        DrawerLayout drawer = (DrawerLayout)activity.findViewById(R.id.drawer_layout);

        DisplayAlbums((GridView)this.getView());

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
