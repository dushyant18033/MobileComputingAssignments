package com.mc2022.template.SensorDataModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AccelData")
public class AccelData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public String timestamp;

    @ColumnInfo(name = "x")
    public float x;

    @ColumnInfo(name = "y")
    public float y;

    @ColumnInfo(name = "z")
    public float z;

    public AccelData(String timestamp, float x, float y, float z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "AccelData{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
