package com.example.mymoviememoir.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mymoviememoir.dao.MovieWatchlistDAO;
import com.example.mymoviememoir.database.MovieWatchlistDatabase;
import com.example.mymoviememoir.watchlistEntity.MovieWatchlist;

import java.util.List;

public class MovieWatchlistRepository {

    private MovieWatchlistDAO dao;
    private LiveData<List<MovieWatchlist>> allMovies;
    private MovieWatchlist movie;

    public MovieWatchlistRepository(Application application){
        MovieWatchlistDatabase db = MovieWatchlistDatabase.getInstance(application);
        dao = db.movieWatchlistDAO();
    }

    public LiveData<List<MovieWatchlist>> getAllMovies(int personId){
        allMovies = dao.getAll(personId);
        return allMovies;
    }

    public void insert(final MovieWatchlist movie){
        MovieWatchlistDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run(){
                dao.insert(movie);
            }
        });
    }

//    public void deleteAll(){
//        MovieWatchlistDatabase.databaseWriteExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                dao.deleteAll();
//            }
//        });
//    }

    public void delete(final MovieWatchlist movie){
        MovieWatchlistDatabase.databaseWriteExecutor.execute(new Runnable(){
            @Override
            public void run(){
                dao.delete(movie);
            }
        });
    }



}
