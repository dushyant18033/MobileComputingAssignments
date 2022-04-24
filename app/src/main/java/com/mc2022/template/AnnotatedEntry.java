package com.mc2022.template;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.HashMap;

public class AnnotatedEntry implements Serializable
{
    private String locId;
    private float locX;
    private float locY;
    private HashMap<String, Integer> rssiInfo;

    public AnnotatedEntry(String locId, float locX, float locY, HashMap<String, Integer> rssiInfo) {
        this.locId = locId;
        this.locX = locX;
        this.locY = locY;
        this.rssiInfo = rssiInfo;
    }

    // TODO: Implement
    @RequiresApi(api = Build.VERSION_CODES.N)
    public float matchRssi(HashMap<String, Integer> rssiInfoOther)
    {
        float res = 100;

        rssiInfoOther.forEach( (key, value) -> {
            if (rssiInfo.containsKey(key))
            {

            }
        });

        return res;
    }

    public String getUserAnnotation()
    {
        return locId;
    }

    public float getLocX() {
        return locX;
    }

    public float getLocY() {
        return locY;
    }
}
