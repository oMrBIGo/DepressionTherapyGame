package com.depressiontherapygame.Users.GameTetris.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.depressiontherapygame.Users.GameTetris.db.tebles.GameLevel;

import java.util.List;

@Dao
public interface DatabaseAccessObject {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLevel(GameLevel... gameLevels);

    @Query("SELECT * FROM GameLevel")
    LiveData<List<GameLevel>> getAllLevel();

    @Query("UPDATE gamelevel SET status=:status , score=:score WHERE level==:level")
    void updatePuzzleStatus(int level, String status, int score);

    @Query("UPDATE gamelevel SET score=:score WHERE level==:level")
    void updatePuzzleScore(int level, int score);
    /*@Query("SELECT noOfBoard FROM gamelevel WHERE level==:level")
    int getNoOfBoard(int level);

    @Query("SELECT noOfColor FROM gamelevel WHERE level==:level")
    int getNoOfColor(int level);*/
    @Query("SELECT * FROM GameLevel")
    List<GameLevel> getAllScore();

    @Query("SELECT score FROM gamelevel WHERE level==:level")
    int getLevelScore(int level);


}
