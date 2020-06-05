package com.example.mymoviememoir.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mymoviememoir.dao.MovieWatchlistDAO;
import com.example.mymoviememoir.watchlistEntity.MovieWatchlist;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MovieWatchlist.class}, version = 2, exportSchema = false)
public abstract class MovieWatchlistDatabase extends RoomDatabase {
    public abstract MovieWatchlistDAO movieWatchlistDAO();
    private static MovieWatchlistDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized MovieWatchlistDatabase getInstance(final Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),MovieWatchlistDatabase.class,
                    "MovieWatchlistDatabase").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }


}
