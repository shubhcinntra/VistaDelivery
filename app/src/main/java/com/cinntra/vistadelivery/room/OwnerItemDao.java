package com.cinntra.vistadelivery.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cinntra.vistadelivery.model.OwnerItem;

import java.util.List;

@Dao
public interface OwnerItemDao {
    @Query("SELECT * FROM table_owner_item")
    List<OwnerItem> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<OwnerItem> data);



}
