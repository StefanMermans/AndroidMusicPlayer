package com.app.player.music.stefan.androidmusicplayer.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.PlayingVisualiser;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.Music.Song;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.MusicQueuePlayer;
import com.app.player.music.stefan.androidmusicplayer.R;
import com.app.player.music.stefan.androidmusicplayer.Callback;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Stefan on 1-5-2017.
 */

public class PlayingFragment extends Fragment implements PlayingVisualiser{
    public static String ARG_ALBUM_FILTER = "alFil";
    public static String ARG_ARTIST_FILTER = "arFil";
    public static String ARG_SONG_INDEX_FILTER = "soFil";

    private String albumFilter;
    private String artistFilter;
    private String currentSong;
    private Callback callback;

    private boolean initialised;

    private class playButton implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            MusicQueuePlayer musicQueuePlayer = callback.GetMusicQuePlayer();
            ViewFlipper songView = (ViewFlipper)getActivity().findViewById(R.id.song_flipper);

            switch (view.getId()) {
                case R.id.button_play:
                    if (musicQueuePlayer.isPlaying()) {
                        musicQueuePlayer.pause();
                        ((ImageButton) view).setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    } else {
                        musicQueuePlayer.start();
                        ((ImageButton) view).setImageResource(R.drawable.ic_pause_black_24dp);
                    }
                    return;
                case R.id.button_next:
                    musicQueuePlayer.Next();
                    break;
                case R.id.button_prev:
                    musicQueuePlayer.Prev();
                    break;
                default:
                    break;
            }

        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Toolbar toolbar = (Toolbar)this.getActivity().findViewById(R.id.toolbar);
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Listening");
            }
        });

        Bundle args = getArguments();
        if(args != null){
            albumFilter = args.getString(ARG_ALBUM_FILTER);
            artistFilter = args.getString(ARG_ARTIST_FILTER);
            currentSong = args.getString(ARG_SONG_INDEX_FILTER);
        } else{
            currentSong = null;
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout listView = (LinearLayout) inflater.inflate(R.layout.frag_playing, container, false);

        return listView;
    }

    @Override
    public void onStart() {
        // Button listeners
        final ImageButton button = (ImageButton) getActivity().findViewById(R.id.button_play);
        final ImageButton backButton = (ImageButton) getActivity().findViewById(R.id.button_prev);
        final ImageButton nextButton = (ImageButton) getActivity().findViewById(R.id.button_next);
        playButton playButton = new playButton();
        button.setOnClickListener(playButton);
        backButton.setOnClickListener(playButton);
        nextButton.setOnClickListener(playButton);


        if(!initialised){
            initialised = true;
            DisplaySongs((LinearLayout)this.getView());
        }

        super.onStart();
    }

    private void DisplaySongs(LinearLayout view){
        ViewFlipper songView = (ViewFlipper)view.findViewById(R.id.song_flipper);


        Song[] songs = callback.GetMusicQuePlayer().GetSongQueue();
        for (int i = 0; i < songs.length; i++)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View song_playing = inflater.inflate(R.layout.song_playing,null);

            // Album art
            ImageView imageView = (ImageView)song_playing.findViewById(R.id.album_art);
            Bitmap bitmap = BitmapFactory.decodeFile(songs[i].getAlbumArt());
            imageView.setImageBitmap(bitmap);

            // Album name
            TextView textView = (TextView)song_playing.findViewById(R.id.song_name);
            textView.setText(songs[i].getTitle());

            songView.addView(song_playing,i);
        }

        songView.setDisplayedChild(callback.GetMusicQuePlayer().getCurrentSongIndex());

        if(songs.length <= 0) return;

        callback.GetMusicQuePlayer().RegisterSeekBar((SeekBar)songView.getCurrentView().findViewById(R.id.seekBar));
        callback.GetMusicQuePlayer().setPlayingVisualiser(this);
        ImageButton button = (ImageButton)getActivity().findViewById(R.id.button_play);

        if(callback.GetMusicQuePlayer().isPlaying()){
            button.setImageResource(R.drawable.ic_pause_black_24dp);
        } else{
            button.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
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

        final Toolbar toolbar = (Toolbar)this.getActivity().findViewById(R.id.toolbar);
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Listening");
            }
        });

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            callback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void Next() {
        ViewFlipper songView = (ViewFlipper)getActivity().findViewById(R.id.song_flipper);
        songView.showNext();
        callback.GetMusicQuePlayer().RegisterSeekBar((SeekBar)songView.getCurrentView().findViewById(R.id.seekBar));

    }

    @Override
    public void Prev(){
        ViewFlipper songView = (ViewFlipper)getActivity().findViewById(R.id.song_flipper);
        songView.showPrevious();
        callback.GetMusicQuePlayer().RegisterSeekBar((SeekBar)songView.getCurrentView().findViewById(R.id.seekBar));
    }
}
