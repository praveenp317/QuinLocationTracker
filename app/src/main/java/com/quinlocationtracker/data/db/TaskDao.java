package com.quinlocationtracker.data.db;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.quinlocationtracker.data.model.LocationTaskModel;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM LocationTaskModel")
    List<LocationTaskModel> getAll();

    @Insert
    void insert(LocationTaskModel locationTaskModel);

    @Delete
    void delete(LocationTaskModel locationTaskModel);


    @Update
    void update(LocationTaskModel locationTaskModel);

}
