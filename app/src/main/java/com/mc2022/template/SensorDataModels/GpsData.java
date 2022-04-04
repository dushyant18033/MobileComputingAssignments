package com.mc2022.template.SensorDataModels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GpsData")
public class GpsData {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "latitude")
    public float latitude;

    @ColumnInfo(name = "longitude")
    public float longitude;

    @ColumnInfo(name = "address")
    public String address;

    public GpsData(String name, float latitude, float longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    @Override
    public String toString() {
        return "GpsData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                '}';
    }
}
