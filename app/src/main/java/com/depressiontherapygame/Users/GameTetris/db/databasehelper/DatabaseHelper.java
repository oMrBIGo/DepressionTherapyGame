package com.depressiontherapygame.Users.GameTetris.db.databasehelper;

import android.content.Context;

import androidx.lifecycle.LiveData;


import com.depressiontherapygame.Users.GameTetris.db.DepressionTetrisDatabase;
import com.depressiontherapygame.Users.GameTetris.db.dao.DatabaseAccessObject;
import com.depressiontherapygame.Users.GameTetris.db.tebles.GameLevel;

import java.util.List;


public class DatabaseHelper {

    private DatabaseAccessObject databaseAccessObject;

    public DatabaseHelper(Context context) {
        DepressionTetrisDatabase db = DepressionTetrisDatabase.getDatabase(context.getApplicationContext());
        databaseAccessObject = db.myDatabaseAccessObject();
    }

    public void insertLevel(GameLevel gameLevel) {
        databaseAccessObject.insertLevel(gameLevel);
    }

    public LiveData<List<GameLevel>> getAllLevel() {
        return databaseAccessObject.getAllLevel();
    }
    public List<GameLevel> getAllScore() {
        return databaseAccessObject.getAllScore();
    }
    public void updatePuzzleStatus(int level, String status, int score) {
        databaseAccessObject.updatePuzzleStatus(level, status, score);
    }

    public void updatePuzzleScore(int level, int score) {
        databaseAccessObject.updatePuzzleScore(level, score);
    }
/*    public int getNoOfBoardByLevel(int level) {
        return databaseAccessObject.getNoOfBoard(level);
    }

    public int getNoOfColorByLevel(int level) {
        return databaseAccessObject.getNoOfColor(level);
    }*/

    public int getLevelStatusById(int level) {
        return databaseAccessObject.getLevelScore(level);
    }
}
