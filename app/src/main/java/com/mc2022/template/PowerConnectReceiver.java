package com.mc2022.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PowerConnectReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "Power Connected");
        Toast.makeText(context, "Power Connected", Toast.LENGTH_SHORT).show();
        context.stopService(new Intent(context, NewsService.class));
    }
}