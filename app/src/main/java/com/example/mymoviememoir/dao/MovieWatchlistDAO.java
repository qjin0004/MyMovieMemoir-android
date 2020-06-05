package com.example.mymoviememoir.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mymoviememoir.watchlistEntity.MovieWatchlist;

import java.util.List;

@Dao
public interface MovieWatchlistDAO {

    @Query("SELECT * FROM moviewatchlist WHERE person_id = :personId")
    LiveData<List<MovieWatchlist>> getAll(int personId);

    @Insert
    long insert(MovieWatchlist movieWatchlist);

    @Delete
    void delete(MovieWatchlist movieWatchlist);

//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    void updateMovie(MovieWatchlist movieWatchlist);

}
