package com.example.mymoviememoir.adapter;

import androidx.recyclerview.selection.ItemDetailsLookup;

public class MemoirResultDetail extends ItemDetailsLookup.ItemDetails {
    private final int adapterPosition;
    private final MemoirInList selectionKey;

    public MemoirResultDetail(int adapterPosition, MemoirInList selectionKey) {
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