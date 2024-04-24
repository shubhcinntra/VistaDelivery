package com.cinntra.vistadelivery.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.cinntra.vistadelivery.model.Countries;

@Database(entities = {Countries.class},version = 1)
public abstract class CountryDatabase extends RoomDatabase {
    public abstract CountryDao myDataDao();
}
