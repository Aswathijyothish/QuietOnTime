package com.ashgeek.quietontime;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Aswathi on 2/22/2016.
 */
public class PrefrencesDB {

    private SharedPreferences sharedpreferences;
    private static String PREF_NAME = "PrefsFile";


    public PrefrencesDB() {
        // Blank
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public static int getStartHour(Context context) {
        return getPrefs(context).getInt("start_time_hour_key", 0);
    }

    public static int getStartMinute(Context context) {
        return getPrefs(context).getInt("start_time_minute_key", 0);
    }
    public static int getEndHour(Context context) {
        return getPrefs(context).getInt("end_time_hour_key", 0);
    }
    public static int getEndMinute(Context context) {
        return getPrefs(context).getInt("end_time_minute_key", 0);
    }
    public static boolean getIsChecked(Context context){
        return getPrefs(context).getBoolean("checked_key", false);
    }
    public static boolean getStatus(Context context){
        return getPrefs(context).getBoolean("status_key", false);
    }
    public static long getStartTime(Context context){
        return getPrefs(context).getLong("start_time_key", 0);
    }
    public static long getEndTime(Context context){
        return getPrefs(context).getLong("end_time_key", 0);
    }



    public static void setTimes(Context context, int startTimeHour,int startTimeMinute,
                                int endTimeHour,int endTimeMinute,Boolean checked) {


        SharedPreferences.Editor editor = getPrefs(context).edit();

        editor.putInt("start_time_hour_key", startTimeHour);
        editor.putInt("end_time_hour_key", endTimeHour);
        editor.putInt("start_time_minute_key", startTimeMinute);
        editor.putInt("end_time_minute_key", endTimeMinute);
        editor.putBoolean("checked_key",checked);
        editor.commit();
    }

    public static void setStatus(Context context,Boolean status){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean("status_key",status);
        editor.commit();
    }

    public static void setEventTimes(Context context,long startTime,long endTime){
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putLong("start_time_key", startTime);
        editor.putLong("end_time_key",endTime);
        editor.commit();
    }
}

