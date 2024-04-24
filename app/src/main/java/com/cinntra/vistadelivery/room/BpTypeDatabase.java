package com.cinntra.vistadelivery.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cinntra.vistadelivery.model.UTypeData;


@Database(entities = {UTypeData.class},version = 1,exportSchema = false)
public abstract class BpTypeDatabase extends RoomDatabase {
    public abstract BpTypeDao myDataDao();
    private static volatile BpTypeDatabase bpTypeDatabase;

     public static BpTypeDatabase getDatabase(final Context context) {
        if (bpTypeDatabase == null) {
            synchronized (BpTypeDatabase.class) {
                if (bpTypeDatabase == null) {
                    bpTypeDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    BpTypeDatabase.class, "my-db-bp_type")
                            .allowMainThreadQueries().build();
                }
            }
        }
        return bpTypeDatabase;
    }

}
