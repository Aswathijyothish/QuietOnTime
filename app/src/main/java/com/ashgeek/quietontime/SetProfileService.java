package com.ashgeek.quietontime;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;


public class SetProfileService extends IntentService {

    private static final String ACTION_SET = "SET";
    private static final String ACTION_UNSET = "UNSET";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    public SetProfileService() {
        super("SetProfileService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SET.equals(action)) {
                if(am.getRingerMode()!=AudioManager.RINGER_MODE_SILENT) {
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    sendNotification("Profile has been set to Silent");
                }else{
                    sendNotification("Profile in Silent Mode");
                }
            } else if (ACTION_UNSET.equals(action)) {
                if(am.getRingerMode()!=AudioManager.RINGER_MODE_NORMAL) {
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    sendNotification("Profile has been set to Normal");
                }else{
                    sendNotification("Profile in Normal Mode");
                }
                PrefrencesDB.setStatus(this, false);
            }
        }
        SetEventReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
