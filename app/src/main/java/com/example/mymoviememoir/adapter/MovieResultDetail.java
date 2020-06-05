package com.example.mymoviememoir.adapter;

import androidx.recyclerview.selection.ItemDetailsLookup;

import com.example.mymoviememoir.model.MovieResult;

public class MovieResultDetail extends ItemDetailsLookup.ItemDetails {
    private final int adapterPosition;
    private final MovieResult selectionKey;

    public MovieResultDetail(int adapterPosition, MovieResult selectionKey) {
        this.adapterPosition = adapterPosition;
        this.selectionKey = selectionKey;
    }


    @Override
    public int getPosition() {
        return adapterPosition;
    }

    @Override
    public Object getSelectionKey() {
        return selectionKey;
    }
}
