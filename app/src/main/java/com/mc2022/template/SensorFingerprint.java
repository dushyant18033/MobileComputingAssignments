package com.mc2022.template;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;

public class SensorFingerprint implements Serializable {

    private String locId;
    private float[] acc = new float[3];
    private float[] mag = new float[3];
    private float[] light = new float[1];

    public SensorFingerprint(String locId, float[] acc, float[] mag, float[] light) {
        this.locId = locId;

        for(int i=0; i<3; i++)
        {
            this.acc[i] = acc[i];
            this.mag[i] = mag[i];
        }

        this.light[0] = light[0];
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double distFingerprint(float[] accOther, float[] magOther, float[] lightOther)
    {
        double acc_dist = 0;
        for(int i=0; i<3; i++)
        {
            float diff = (acc[i] - accOther[i]);
            acc_dist += diff*diff;
        }
        acc_dist = Math.sqrt(acc_dist);

        double mag_dist = 0;
        for(int i=0; i<3; i++)
        {
            float diff = (mag[i] - magOther[i]);
            mag_dist += diff*diff;
        }
        mag_dist = Math.sqrt(mag_dist);

        double light_dist = Math.abs(light[0] - lightOther[0]);

        return acc_dist + mag_dist + light_dist;
    }

    public String getUserAnnotation()
    {
        return locId;
    }
}
