package com.ashgeek.quietontime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

public class SetEventReceiver extends WakefulBroadcastReceiver {
    private AlarmManager alarmMgr;
    private PendingIntent setAlarmIntent;
    private PendingIntent unsetAlarmIntent;
    final private int _PENDING_SET_ID = 1;
    final private int _PENDING_UNSET_ID = 2;

    public SetEventReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, SetProfileService.class);

        Bundle extras = intent.getExtras();
        String action = extras.getString("Action");

        if (action.equals("Set")) {
            service.setAction("SET");
        } else {
            service.setAction("UNSET");
        }
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
        // END_INCLUDE(alarm_onreceive)
    }

    public void SetEvent(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SetEventReceiver.class);
        intent.putExtra("Action", "Set");
        setAlarmIntent = PendingIntent.getBroadcast(context, _PENDING_SET_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(PrefrencesDB.getIsChecked(context)!=true) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, PrefrencesDB.getStartTime(context), setAlarmIntent);
        }
        else{
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    PrefrencesDB.getStartTime(context), AlarmManager.INTERVAL_DAY, setAlarmIntent);
        }
        setBootEvent(context, "ENABLE");
    }

    public void UnsetEvent(Context context) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SetEventReceiver.class);
        intent.putExtra("Action", "UnSet");
        unsetAlarmIntent = PendingIntent.getBroadcast(context, _PENDING_UNSET_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(PrefrencesDB.getIsChecked(context)!=true) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, PrefrencesDB.getEndTime(context), unsetAlarmIntent);

        }
        else{
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    PrefrencesDB.getEndTime(context), AlarmManager.INTERVAL_DAY, unsetAlarmIntent);
        }
        setBootEvent(context, "ENABLE");
    }

    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(setAlarmIntent);
            alarmMgr.cancel(unsetAlarmIntent);
        }
        PrefrencesDB.setStatus(context,false);
        PrefrencesDB.setTimes(context,0,0,0,0,false);
        setBootEvent(context, "DISABLE");
    }

    private void setBootEvent(Context context, String state) {
        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        if (state.equals("ENABLE")) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else if (state.equals("DISABLE")) {
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }


    }
}