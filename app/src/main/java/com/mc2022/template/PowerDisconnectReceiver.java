package com.mc2022.template;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PowerDisconnectReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Receiver", "Power Disconnected");
        Toast.makeText(context, "Power Disconnected", Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context, NewsService.class));
    }
}