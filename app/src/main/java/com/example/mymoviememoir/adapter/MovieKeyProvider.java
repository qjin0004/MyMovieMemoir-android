package com.example.mymoviememoir.adapter;

import androidx.recyclerview.selection.ItemKeyProvider;

import com.example.mymoviememoir.model.MovieResult;

import java.util.List;

public class MovieKeyProvider extends ItemKeyProvider {
    private final List<MovieResult> movieResults;

    public MovieKeyProvider(int scope, List<MovieResult> movieResults){
        super(scope);
        this.movieResults = movieResults;
    }

    @Override
    public Object getKey(int position){
        return movieResults.get(position);
    }

    @Override
    public int getPosition(Object key){
        return movieResults.indexOf(key);
    }
}
