package com.mc2022.template.SensorDataModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "LightData")
public class LightData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public String timestamp;

    @ColumnInfo(name = "light")
    public float light;

    public LightData(String timestamp, float light) {
        this.timestamp = timestamp;
        this.light = light;
    }

    @Override
    public String toString() {
        return "ProximityData{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                ", light=" + light +
                '}';
    }
}
