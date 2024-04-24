package com.cinntra.vistadelivery.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cinntra.vistadelivery.model.Role;

import java.util.List;

@Dao
public interface RoleDao {
    @Query("SELECT * FROM table_Role")
    List<Role> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Role> data);



}
