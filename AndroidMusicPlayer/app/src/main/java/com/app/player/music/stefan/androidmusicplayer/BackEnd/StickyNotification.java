package com.app.player.music.stefan.androidmusicplayer.BackEnd;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.app.player.music.stefan.androidmusicplayer.R;

/**
 * Created by Stefan on 3-5-2017.
 */

public class StickyNotification {
    private static final String PAUSE = "Pause";
    private static final String PREVIOUS = "Previous";
    private static final String NEXT = "Next";
    private static final String PLAY = "Play";

    private Notification notification;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public MusicQueuePlayer musicQueuePlayer;

    private int identifier;

    public static class NotificationHandler extends IntentService{
        public NotificationHandler(){
            super(NotificationHandler.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            if(intent == null)return;
            String action = intent.getAction();

            switch (action){
                case PAUSE:
                    // TODO Pause song
                    break;
                case PREVIOUS:
                    // TODO Previous song
                    break;
                case NEXT:
                    // TODO Next song
                    break;
                case PLAY:
                    // TODO Play music
                    break;
                default:
                    break;
            }
        }
    }

    public StickyNotification(Context context, MusicQueuePlayer musicQueuePlayer){
        this.musicQueuePlayer = musicQueuePlayer;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        identifier = 1; // Does this number matter?

        Intent actionIntent = new Intent(context,NotificationHandler.class).setAction(PAUSE);
        PendingIntent pauseResultIntent =
                PendingIntent.getService(context,0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        actionIntent = new Intent(context, NotificationHandler.class).setAction(PREVIOUS);
        PendingIntent previousResultIntent =
                PendingIntent.getService(context,0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        actionIntent = new Intent(context, NotificationHandler.class).setAction(NEXT);
        PendingIntent nextResultIntent =
                PendingIntent.getService(context,0,actionIntent,PendingIntent.FLAG_UPDATE_CURRENT);



        // Initialise the builder
        builder = new NotificationCompat.Builder(context); // Init
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // Visibility
        builder.setContentTitle("Sky music"); // Application title
        builder.setContentText("Song name"); // Name of the song
        builder.setSmallIcon(R.mipmap.ic_launcher); // Application icon

        // Crete the icons
        builder.addAction(R.drawable.ic_skip_previous_black_24dp, PREVIOUS, previousResultIntent);
        builder.addAction(R.drawable.ic_pause_black_24dp, PAUSE, pauseResultIntent);
        builder.addAction(R.drawable.ic_skip_next_black_24dp, NEXT, nextResultIntent);

        builder.setStyle(new NotificationCompat.MediaStyle()); // Set the style



    }

    public void PushNotification(String name, String album, boolean sticky){
        notificationManager.cancel(identifier); // Cancel any active notification

        builder.setContentText(name);
        Bitmap bitmap = BitmapFactory.decodeFile(album);
        builder.setLargeIcon(bitmap);
        builder.setAutoCancel(true);

        notification = builder.build(); // Build the notification

        if(sticky)
            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT; // Flags

        notificationManager.notify(identifier,notification); // Send the new notification
    }

    public void Stop(){
        notificationManager.cancelAll(); // Cancel any active notification
    }
}
