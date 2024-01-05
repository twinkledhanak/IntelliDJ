package com.tkd.twinkledhanak.intellidj.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by twinkle dhanak on 1/14/2018.
 */

public class MyReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        // start service again in broadcast receiver

        context.startService(new Intent(context,MyIntentService.class));
    }
}
