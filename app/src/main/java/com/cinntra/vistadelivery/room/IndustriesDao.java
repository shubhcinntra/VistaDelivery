package com.cinntra.vistadelivery.room;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cinntra.vistadelivery.model.IndustryItem;

import java.util.List;

@Dao
public interface IndustriesDao {

    @Query("SELECT * FROM industry")
    List<IndustryItem> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<IndustryItem> data);
}
