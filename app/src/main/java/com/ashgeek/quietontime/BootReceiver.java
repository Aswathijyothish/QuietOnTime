package com.ashgeek.quietontime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Aswathi on 2/25/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    SetEventReceiver setEventReceiver = new SetEventReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            setEventReceiver.SetEvent(context);
            setEventReceiver.UnsetEvent(context);
        }
    }
}
