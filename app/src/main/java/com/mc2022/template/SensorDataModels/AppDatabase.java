package com.mc2022.template.SensorDataModels;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mc2022.template.SensorDataDao;

@Database(entities = {AccelData.class, GyroData.class, LightData.class, ProximityData.class, OrientationData.class, TempData.class, GpsData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SensorDataDao sensorDataDao();
}
