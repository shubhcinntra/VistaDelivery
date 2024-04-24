package com.cinntra.vistadelivery.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cinntra.vistadelivery.model.ItemCategoryData;

import java.util.List;

@Dao
public interface ItemCategoryDao {
    @Query("SELECT * FROM table_item_category")
    List<ItemCategoryData> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemCategoryData> data);



}
