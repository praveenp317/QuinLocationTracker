package com.quinlocationtracker.data.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.quinlocationtracker.data.model.LocationTaskModel;

@Database(entities = {LocationTaskModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
