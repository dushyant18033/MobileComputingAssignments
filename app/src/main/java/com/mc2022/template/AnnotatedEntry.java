package com.mc2022.template;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.HashMap;

public class AnnotatedEntry implements Serializable
{
    public static HashMap<String, Integer> currentRssi;

    private String locId;
    private HashMap<String, Integer> rssiInfo;

    public AnnotatedEntry(String locId, HashMap<String, Integer> rssiInfo) {
        this.locId = locId;
        this.rssiInfo = rssiInfo;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double distRssi(HashMap<String, Integer> rssiInfoCur)
    {
        final double[] dist = {0};

        final int[] matches = {0};

        rssiInfoCur.forEach( (key, value) -> {
            if (rssiInfo.containsKey(key))
            {
                dist[0] += Math.pow(rssiInfo.get(key) - rssiInfoCur.get(key), 2);
                matches[0]++;
            }
        });
        return Math.sqrt(dist[0]/matches[0]);
    }

    public String getUserAnnotation()
    {
        return locId;
    }
}
