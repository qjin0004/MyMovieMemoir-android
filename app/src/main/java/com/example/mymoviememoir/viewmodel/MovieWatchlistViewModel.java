package com.example.mymoviememoir.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mymoviememoir.repository.MovieWatchlistRepository;
import com.example.mymoviememoir.watchlistEntity.MovieWatchlist;

import java.util.List;

public class MovieWatchlistViewModel extends ViewModel {
    private MovieWatchlistRepository movieWatchlistRepository;
    private MutableLiveData<List<MovieWatchlist>> allMovieWatchlists;

    public MovieWatchlistViewModel (){
        allMovieWatchlists = new MutableLiveData<>();
    }

    public void setMovies(List<MovieWatchlist> movies){
        allMovieWatchlists.setValue(movies);
    }

    public LiveData<List<MovieWatchlist>> getAllMovies(int personId){
        return movieWatchlistRepository.getAllMovies(personId);
    }

    public void initializeVars(Application application){
        movieWatchlistRepository = new MovieWatchlistRepository(application);
    }

    public void insert(MovieWatchlist movieWatchlist){
        movieWatchlistRepository.insert(movieWatchlist);
    }

    public void delete(MovieWatchlist movieWatchlist){
        movieWatchlistRepository.delete(movieWatchlist);
    }

//    public void update(MovieWatchlist...)

}
