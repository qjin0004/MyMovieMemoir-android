package com.example.mymoviememoir.adapter;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemoirInList {
    private int movieId;
    private String movieName;
    private String releaseDate;
    private String image;
    private String watchDate;
    private int position;
    private String comment;
    private float rating;
    private float publicRating;
    private String[] genres;

    public MemoirInList(int movieId, String movieName,String releaseDate,String image,String watchDate,int position,String comment, float rating,float publicRating,String[] genres){
        this.movieName = movieName;
        this.movieId = movieId;
        this.releaseDate = releaseDate;
        this.image = image;
        this.watchDate = watchDate;
        this.position = position;
        this.comment = comment;
        this.rating = rating;
        this.publicRating = publicRating;
        this.genres = genres;
    }

    public static List<MemoirInList> createContactsList(){
        List<MemoirInList> memoirInLists = new ArrayList<>();
        return memoirInLists;
    }


    public String getMovieName() {
        return movieName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getImage() {
        return image;
    }

    public String getWatchDate() {
        return watchDate;
    }

    public int getPosition() {
        return position;
    }

    public String getComment() {
        return comment;
    }

    public float getRating() {
        return rating;
    }


    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setWatchDate(String watchDate) {
        this.watchDate = watchDate;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getPublicRating() {
        return publicRating;
    }

    public void setPublicRating(float publicRating) {
        this.publicRating = publicRating;
    }


    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

}
