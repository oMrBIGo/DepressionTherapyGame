package com.depressiontherapygame.Users.GameTetris.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.depressiontherapygame.Users.GameTetris.db.dao.DatabaseAccessObject;
import com.depressiontherapygame.Users.GameTetris.db.tebles.GameLevel;
import com.fstyle.library.helper.AssetSQLiteOpenHelperFactory;

/**
 * Created on 11/11/2021.
 */
@Database(entities = {GameLevel.class}, version = 1, exportSchema = false)
public abstract class DepressionTetrisDatabase extends RoomDatabase {

    public abstract DatabaseAccessObject myDatabaseAccessObject();

    private static DepressionTetrisDatabase INSTANCE;

    public static DepressionTetrisDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DepressionTetrisDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DepressionTetrisDatabase.class,
                            "depression_tetris.db")
                            .openHelperFactory(new AssetSQLiteOpenHelperFactory())
                            .allowMainThreadQueries()
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}