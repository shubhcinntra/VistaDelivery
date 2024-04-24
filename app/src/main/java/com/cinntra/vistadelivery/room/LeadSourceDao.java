package com.cinntra.vistadelivery.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cinntra.vistadelivery.model.LeadTypeData;

import java.util.List;

@Dao
public interface LeadSourceDao {
    @Query("SELECT * FROM data_lead_type")
    List<LeadTypeData> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LeadTypeData> data);



}
