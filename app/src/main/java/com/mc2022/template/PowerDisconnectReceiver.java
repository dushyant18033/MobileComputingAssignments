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
        Intent intent1 = new Intent(context, NewsService.class);
        intent1.putExtra("start_index", countValidFiles(context));
        context.startService(intent1);
    }

    private int countValidFiles(Context context)
    {
        String[] files = context.fileList();

        int ctr = 0;
        for (int i=0; i<files.length; i++)
        {
            if (files[i].startsWith("news"))
            {
                ctr++;
            }
        }

        return ctr;
    }
}