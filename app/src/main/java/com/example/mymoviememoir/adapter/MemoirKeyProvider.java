package com.example.mymoviememoir.adapter;

import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.List;

public class MemoirKeyProvider extends ItemKeyProvider {
    private final List<MemoirInList> movies;

    public MemoirKeyProvider(int scope, List<MemoirInList> movies){
        super(scope);
        this.movies = movies;
    }

    @Override
    public Object getKey(int position){
        return movies.get(position);
    }

    @Override
    public int getPosition(Object key){
        return movies.indexOf(key);
    }
}
