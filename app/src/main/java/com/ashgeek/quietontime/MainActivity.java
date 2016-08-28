package com.ashgeek.quietontime;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class MainActivity extends Activity {

    TextView st_tv;
    TextView ed_tv;
    TextView stt_tv;
    TextView edt_tv;
    CheckBox rpt_cb;
    ImageButton help_btn;
    Button slt_btn;
    Button cancel_btn;
    TimePicker st_time;
    TimePicker ed_time;
    Intent intn;
    long startTime;
    long endTime;

    SetEventReceiver setEventReceiver = new SetEventReceiver();
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        st_time = (TimePicker)findViewById(R.id.StarttimePicker);
        ed_time = (TimePicker)findViewById(R.id.EndtimePicker);
        st_tv=(TextView)findViewById(R.id.starttimetxt);
        ed_tv=(TextView)findViewById(R.id.endtimetxt);
        stt_tv=(TextView)findViewById(R.id.starttxt);
        edt_tv=(TextView)findViewById(R.id.endtxt);
        rpt_cb=(CheckBox)findViewById(R.id.repeat_cb);
        help_btn=(ImageButton)findViewById(R.id.help_button);
        slt_btn=(Button)findViewById(R.id.silent_button);
        cancel_btn=(Button)findViewById(R.id.cancel_button);

        slt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefrencesDB.setTimes(mContext, st_time.getCurrentHour(), st_time.getCurrentMinute(), ed_time.getCurrentHour(),
                        ed_time.getCurrentMinute(), rpt_cb.isChecked());
                PrefrencesDB.setStatus(mContext, true);
                startTime = convertToMillis(PrefrencesDB.getStartHour(mContext), PrefrencesDB.getStartMinute(mContext));
                endTime = convertToMillis(PrefrencesDB.getEndHour(mContext), PrefrencesDB.getEndMinute(mContext));
                String toastMessage=compareTimes(startTime,endTime);
                setTimes(PrefrencesDB.getStartHour(mContext),PrefrencesDB.getStartMinute(mContext),st_tv);
                setTimes(PrefrencesDB.getEndHour(mContext), PrefrencesDB.getEndMinute(mContext), ed_tv);
                setEventReceiver.SetEvent(mContext);
                setEventReceiver.UnsetEvent(mContext);
                buildAlert(toastMessage);
                        }

        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEventReceiver.cancelAlarm(mContext);
                resetTimes();
                buildAlert("SmartMute settings cancelled");
            }

        });

        help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intn = new Intent(mContext,HelpActivity.class);
                startActivity(intn);
            }
        });

    }
    public  String compareTimes(long startTime ,long endTime){
        String toastMessage=null;
        String day = null;
        day = "today";

        if(startTime>System.currentTimeMillis()){
            if(endTime < startTime && endTime> System.currentTimeMillis() &&PrefrencesDB.getIsChecked(mContext) ){
                // endTime = endTime + 24 * 60 * 60 * 1000;
                day="today";
            }
            else if(endTime < startTime){
                endTime = endTime + 24 * 60 * 60 * 1000;
            }
        }
        else{
            day="tomorrow.";
            if(endTime > startTime && endTime> System.currentTimeMillis() && PrefrencesDB.getIsChecked(mContext) ){
                // endTime = endTime + 24 * 60 * 60 * 1000;

                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                if(am.getRingerMode()!=AudioManager.RINGER_MODE_SILENT) {
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                   buildAlert("Profile set to silent mode from now");
                }else{
                    buildAlert("Profile in silent mode now");
                }

               day="today";
            }


            else if(endTime > startTime  ){
                endTime = endTime + 24 * 60 * 60 * 1000;
            }
            else if(endTime < startTime && PrefrencesDB.getIsChecked(mContext) ){
                endTime = endTime + 24 * 60 * 60 * 1000;
            }
            else{
                endTime = endTime +2* 24 * 60 * 60 * 1000;
            }
            startTime = startTime + 24 * 60 * 60 * 1000;
        }

        PrefrencesDB.setEventTimes(mContext, startTime, endTime);
        if(PrefrencesDB.getIsChecked(mContext)){
            toastMessage= "SmartMute set from "+day;
        }else
        {
            toastMessage= "SmartMute set for "+day;
        }
        return toastMessage;
    }

    @Override
    protected void onResume(){
        super.onResume();

        if((PrefrencesDB.getStatus(mContext)!=true) && (PrefrencesDB.getIsChecked(mContext)!=true)){
            resetTimes();
        }else{
            setTimes(PrefrencesDB.getStartHour(mContext),PrefrencesDB.getStartMinute(mContext),st_tv);
            setTimes(PrefrencesDB.getEndHour(mContext), PrefrencesDB.getEndMinute(mContext), ed_tv);
        }
    }

    private void setTimes(int hour,int minute,TextView timeTv){
        rpt_cb.setChecked(PrefrencesDB.getIsChecked(mContext));
        stt_tv.setText("Start Mute Time");
        edt_tv.setText("End Mute Time");
        st_time.setCurrentHour(PrefrencesDB.getStartHour(mContext));
        st_time.setCurrentMinute(PrefrencesDB.getStartMinute(mContext));
        ed_time.setCurrentHour(PrefrencesDB.getEndHour(mContext));
        ed_time.setCurrentMinute(PrefrencesDB.getEndMinute(mContext));

        if (hour > 12) {
            if (minute < 10) {
                timeTv.setText(" -    " + (hour - 12) + " : " + " 0" + minute + " PM");
            } else {
                timeTv.setText(" -    " + (hour - 12) + " : " + minute + " PM");
            }
        } else if(hour == 12){
            if (minute < 10) {
                timeTv.setText(" -    " + hour + " : " + " 0" + minute + " PM");
            } else {
                timeTv.setText(" -    " + hour + " : " + minute + " PM");
            }
        }
        else {
            if (minute < 10) {
                timeTv.setText(" -    " + hour + " : " + " 0" + minute + " AM");
            } else {
                timeTv.setText(" -    " + hour + " : " + minute + " AM");
            }
        }

    }

    private void resetTimes(){
        st_tv.setText("");
        ed_tv.setText("");
        stt_tv.setText("Select Start Mute Time");
        edt_tv.setText("Select End Mute Time");
        rpt_cb.setChecked(false);
        Calendar calendar = Calendar.getInstance();
        long now = System.currentTimeMillis()+(60*1000);
        calendar.setTimeInMillis(now);
        st_time.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        st_time.setCurrentMinute(calendar.get(Calendar.MINUTE));
        now=now+(60*1000);
        calendar.setTimeInMillis(now);
        ed_time.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        ed_time.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    private long convertToMillis(int hour,int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return  calendar.getTimeInMillis();

    }

    private void buildAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //builder.setTitle("SmartMute Settings")
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
