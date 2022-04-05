package com.mc2022.template;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.mc2022.template.SensorDataModels.AccelData;
import com.mc2022.template.SensorDataModels.GpsData;
import com.mc2022.template.SensorDataModels.GyroData;
import com.mc2022.template.SensorDataModels.LightData;
import com.mc2022.template.SensorDataModels.OrientationData;
import com.mc2022.template.SensorDataModels.ProximityData;
import com.mc2022.template.SensorDataModels.TempData;

import java.util.List;

@Dao
public interface SensorDataDao {

    @Query("SELECT * FROM GpsData G ORDER BY (ABS(G.latitude - :latitude) + ABS(G.longitude - :longitude)) ASC")
    List<GpsData> getGpsData(double latitude, double longitude);

    @Query("SELECT * FROM GpsData")
    List<GpsData> getAllGps();

    @Insert
    void insertGps(GpsData gpsData);

    @Delete
    void deleteGps(GpsData gpsData);



    @Query("SELECT * FROM AccelData ORDER BY timestamp DESC LIMIT 10")
    List<AccelData> getAcc();

    @Insert
    void insertAcc(AccelData... accData);

    @Delete
    void deleteAcc(AccelData accData);



    @Query("SELECT * FROM GyroData ORDER BY timestamp DESC LIMIT 10")
    List<GyroData> getGyro();

    @Insert
    void insertGyro(GyroData... gyroData);

    @Delete
    void deleteGyro(GyroData gyroData);



    @Query("SELECT * FROM OrientationData ORDER BY timestamp DESC LIMIT 10")
    List<OrientationData> getOrientation();

    @Query("SELECT * FROM OrientationData ORDER BY timestamp DESC LIMIT 1")
    OrientationData getLastOrientation();

    @Insert
    void insertOrientation(OrientationData orientationData);

    @Delete
    void deleteOrientation(OrientationData orientationData);



    @Query("SELECT * FROM LightData ORDER BY timestamp DESC LIMIT 10")
    List<LightData> getLight();

    @Insert
    void insertLight(LightData... lightData);

    @Delete
    void deleteLight(LightData lightData);



    @Query("SELECT * FROM TempData ORDER BY timestamp DESC LIMIT 10")
    List<TempData> getTemp();

    @Insert
    void insertTemp(TempData... tempData);

    @Delete
    void deleteTemp(TempData tempData);



    @Query("SELECT * FROM ProximityData ORDER BY timestamp DESC LIMIT 10")
    List<ProximityData> getProximity();

    @Query("SELECT * FROM ProximityData ORDER BY timestamp DESC LIMIT 3")
    List<ProximityData> getProximityLast3();

    @Insert
    void insertProximity(ProximityData... proximityData);

    @Delete
    void deleteProximity(ProximityData proximityData);
}
