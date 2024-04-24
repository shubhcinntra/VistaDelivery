package com.cinntra.vistadelivery.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cinntra.vistadelivery.model.UTypeData;

import java.util.List;

@Dao
public interface BpTypeDao {
    @Query("SELECT * FROM table_BpTypeData")
    List<UTypeData> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UTypeData> data);



}
