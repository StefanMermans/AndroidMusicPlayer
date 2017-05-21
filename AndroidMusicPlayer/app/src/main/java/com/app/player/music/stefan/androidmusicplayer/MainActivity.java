package com.app.player.music.stefan.androidmusicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.app.player.music.stefan.androidmusicplayer.BackEnd.Adapters.AudioFocusChangeListener;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.MusicQueuePlayer;
import com.app.player.music.stefan.androidmusicplayer.BackEnd.StickyNotification;
import com.app.player.music.stefan.androidmusicplayer.Fragments.AlbumFragment;
import com.app.player.music.stefan.androidmusicplayer.Fragments.ArtistFragment;
import com.app.player.music.stefan.androidmusicplayer.Fragments.PlayingFragment;
import com.app.player.music.stefan.androidmusicplayer.Fragments.SongFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Callback {

    public static final int EXTERNAL_STORAGE_PERMISSION = 20;
    private MusicQueuePlayer musicQueuePlayer;
    private AudioFocusChangeListener audioFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        audioFocusChangeListener = new AudioFocusChangeListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // IMPORTANT!!!!
        musicQueuePlayer = new MusicQueuePlayer(this);
        // !!!!!!!!!!!!!

        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Artists");
            }
        });

        // Check permission to read from external storage
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION);
        } else{
            AttachBaseFragment();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right); // This doesn't seem to work...
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AttachBaseFragment();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_albums) {
            switchFragment(new AlbumFragment());
        } else if (id == R.id.nav_artists) {
            switchFragment(new ArtistFragment());
        } else if (id == R.id.nav_songs) {
            switchFragment(new SongFragment());
        } else if (id == R.id.nav_songs_playing){
            switchFragment(new PlayingFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void AttachBaseFragment(){
//        AlbumFragment albumFragment = new AlbumFragment();
        ArtistFragment albumFragment = new ArtistFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.content_main, albumFragment);

        transaction.commit();
    }

    private void switchFragment(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        transaction.replace(R.id.content_main, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    // Method used by fragments to switch to a different fragment.
    @Override
    public void onItemClicked(Fragment newFragment) {
        switchFragment(newFragment);
    }

    @Override
    public MusicQueuePlayer GetMusicQuePlayer() {
        return musicQueuePlayer;
    }

    @Override
    protected void onPause() {
        boolean finish = isFinishing();

        super.onPause();
    }

    @Override
    protected void onStop() {
        boolean finish = isFinishing();
        if(finish){
            musicQueuePlayer.stop();
        }

        super.onStop();
    }
}

