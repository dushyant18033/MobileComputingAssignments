package com.mc2022.template.SensorDataModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "TempData")
public class TempData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public String timestamp;

    @ColumnInfo(name = "temperature")
    public float temperature;

    public TempData(String timestamp, float temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "TempData{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                ", temperature=" + temperature +
                '}';
    }
}
