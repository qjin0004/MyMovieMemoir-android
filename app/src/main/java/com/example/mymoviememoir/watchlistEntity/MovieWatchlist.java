package com.example.mymoviememoir.watchlistEntity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MovieWatchlist{

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "person_id")
    public int personId;


    @ColumnInfo(name = "movie_id_api")
    public int movieApiId;

    @ColumnInfo(name = "movie_name")
    public String movieName;

    @ColumnInfo(name = "release_date")
    public String releaseDate;

    @ColumnInfo(name = "add_date")
    public String addWatchlistDate;

//    @ColumnInfo(name = "imageURL")
//    public String imageURL;

    public MovieWatchlist(){

    }

    public MovieWatchlist(int personId, int movieApiId, String movieName,String releaseDate, String addWatchlistDate){
        this.personId = personId;
        this.movieApiId = movieApiId;
        this.movieName = movieName;
        this.releaseDate = releaseDate;
        this.addWatchlistDate = addWatchlistDate;
    }

    public int getPk() {
        return id;
    }


    public String getMovieName() {
        return movieName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getAddWatchlistDate() {
        return addWatchlistDate;
    }

    public int getId() {
        return movieApiId;
    }

    public void setId(int movieApiId) {
        this.movieApiId = movieApiId;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setAddWatchlistDate(String addWatchlistDate) {
        this.addWatchlistDate = addWatchlistDate;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }


}
