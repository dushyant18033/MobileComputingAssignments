package com.mc2022.template.SensorDataModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "ProximityData")
public class ProximityData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    public String timestamp;

    @ColumnInfo(name = "proximity")
    public float proximity;

    public ProximityData(String timestamp, float proximity) {
        this.timestamp = timestamp;
        this.proximity = proximity;
    }

    @Override
    public String toString() {
        return "ProximityData{" +
                "id=" + id +
                ", timestamp='" + timestamp + '\'' +
                ", proximity=" + proximity +
                '}';
    }
}
