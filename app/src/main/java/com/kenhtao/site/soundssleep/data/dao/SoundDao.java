package com.kenhtao.site.soundssleep.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kenhtao.site.soundssleep.data.entity.SoundEntity;

import java.util.List;
@Dao
public interface SoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSound(SoundEntity sound);

    @Query("SELECT * FROM sounds")
    List<SoundEntity> getAllSounds();
}
