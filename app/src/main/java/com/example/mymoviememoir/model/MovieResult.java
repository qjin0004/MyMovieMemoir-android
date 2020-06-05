package com.example.mymoviememoir.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieResult implements Parcelable {

    private int movieId;
    private String name;
    private String releaseDate;
    private String image;

    public MovieResult(Parcel in){
        this.movieId = in.readInt();
        this.name = in.readString();
        this.releaseDate = in.readString();
        this.image = in.readString();
    }

    public MovieResult(int movieId,String name,String releaseDate, String image){
        this.movieId = movieId;
        this.name = name;
        this.releaseDate = releaseDate;
        this.image = image;
    }

    public void writeToParcel(Parcel parcel, int flags){
        parcel.writeInt(movieId);
        parcel.writeString(name);
        parcel.writeString(releaseDate);
        parcel.writeString(image);
    }

    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator<MovieResult> CREATOR = new Parcelable.Creator<MovieResult>(){
        @Override
        public MovieResult createFromParcel(Parcel in){
            return new MovieResult(in);
        }
        @Override
        public MovieResult[] newArray(int size){
            return new MovieResult[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

}
