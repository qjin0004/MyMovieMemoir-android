package com.example.mymoviememoir.networkconnection;

import com.example.mymoviememoir.model.MovieResult;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TheMovieDBAPI {
    private OkHttpClient client = null;
    private String results;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public TheMovieDBAPI(){
        client=new OkHttpClient();
    }

    private static final String API_KEY = "18057fe61aa8766edb8df302b41d93db";

    //search movie based on name
    public String search(String keyword){
        //deal with parameter
        keyword = keyword.replace(" ","%20");

        final String methodPath = "https://api.themoviedb.org/3/search/movie?api_key="+API_KEY+"&language=en-US&query="+ keyword + "&page=1&include_adult=false";

        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
        }catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }

    //search movie based on id
    public String movieIdSearch(int movieId){
        final String methodPath = "https://api.themoviedb.org/3/movie/"+movieId+"?api_key=18057fe61aa8766edb8df302b41d93db";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
        }catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }


    //search movie based on movieName and releaseYear  -> get id, image, public rating
    public String movieSearchByNameAndYear(String movieName, int year){
        final String methodPath = "https://api.themoviedb.org/3/search/movie?api_key=18057fe61aa8766edb8df302b41d93db&language=en-US&query="+movieName+"&page=1&include_adult=false&primary_release_year="+year;
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
//        String[] result = new String[3];
        String id = "0";
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
            JSONObject jsonObject = new JSONObject(results);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            id = String.valueOf(jsonArray.getJSONObject(0).getInt("id"));
            String image = "https://image.tmdb.org/t/p/w500" + jsonArray.getJSONObject(0).getString("poster_path");
            String rating = String.valueOf(jsonArray.getJSONObject(0).getDouble("vote_average"));
//            result[0] = id;
//            result[1] = image;
//            result[2] = rating;
        }catch(Exception e){
            e.printStackTrace();
        }
        return id;
    }


    public String[] forMovieMemoir(String movieName, int year){
        String movieId = movieSearchByNameAndYear(movieName,year);  //send get requests
        String results = movieIdSearch(Integer.parseInt(movieId)); //send get requests
        String[] result = new String[4];
        result[0] = movieId;
        try{
            JSONObject jsonObject = new JSONObject(results);
            String genre = getGenresById(results);
            String image = getImageById(results);
            String rating = getPublilcRatingById(results);
            result[1] = image;
            result[2] = rating;
            result[3] = genre;
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }


    //define filter spinner
    public String getGenreList(){
        final String methodPath = "https://api.themoviedb.org/3/genre/movie/list?api_key=18057fe61aa8766edb8df302b41d93db&language=en-US";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
        }catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }

    public ArrayList<String> dealFilterItem() throws JSONException {
        String results = getGenreList();
        ArrayList<String> result = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(results);
        JSONArray jsonArray = jsonObject.getJSONArray("genres");
        for(int index = 0; index < jsonArray.length(); index++){
            result.add(jsonArray.getJSONObject(index).getString("name"));
        }
        return result;
    }



    //for movie search, get info based on movie name search
    public static ArrayList<MovieResult> getInfo(String results) throws JSONException {
        ArrayList<MovieResult> result = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(results);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            if(jsonArray != null && jsonArray.length() > 0){
                for(int index = 0; index < jsonArray.length(); index++){
                    int id = jsonArray.getJSONObject(index).getInt("id");
                    String name = jsonArray.getJSONObject(index).getString("title");
                    String release = jsonArray.getJSONObject(index).getString("release_date");
                    String path = jsonArray.getJSONObject(index).getString("poster_path");
                    String image = "https://image.tmdb.org/t/p/w500" + path;
                    MovieResult movie = new MovieResult(id,name, release,image);
                    result.add(movie);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String getShortStoryById(String results) throws JSONException {
        JSONObject jsonObject = new JSONObject(results);
        String shortStory = jsonObject.getString("overview");
        return shortStory;
    }

    public String getImageById(String results) throws JSONException {
        JSONObject jsonObject = new JSONObject(results);
        String image = "https://image.tmdb.org/t/p/w500" + jsonObject.getString("poster_path");
        return image;
    }

    public String getPublilcRatingById(String results) throws JSONException{
        JSONObject jsonObject = new JSONObject(results);
        String rating = String.valueOf(jsonObject.getDouble("vote_average"));
        return rating;
    }

    //country
    public String getCountryById(String results) throws JSONException {
        JSONObject jsonObject = new JSONObject(results);
        JSONArray countryList = jsonObject.getJSONArray("production_countries");

        //get real value
        String countries = "";
        if(countryList != null && countryList.length() > 0){
            for(int index = 0; index < countryList.length(); index++){
                String country = countryList.getJSONObject(index).getString("name");
                countries += country + "; ";
            }

        }
        return countries;

    }

    //get genres name based on id
    public String getGenresById(String results) throws JSONException {
        //deal with this moive
        JSONObject jsonObject = new JSONObject(results);
        JSONArray genresList = jsonObject.getJSONArray("genres");

        //get real value
        String movieGenres = "";
        if(genresList != null && genresList.length() > 0){
            for(int index = 0; index < genresList.length(); index++){
                String gen = genresList.getJSONObject(index).getString("name");
                movieGenres += gen + "; ";
            }

        }
        return movieGenres;
    }

    //cast prepare for getMovieTop5Cast
    public String getCastDirInfoByID(int movieId){
        final String methodPath = "https://api.themoviedb.org/3/movie/"+movieId+"?api_key="+API_KEY+"&append_to_response=credits";
        Request.Builder builder = new Request.Builder();
        builder.url(methodPath);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
        }catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }

    //get this movie top5 casts
    public String getMovieTop5Casts(String results) throws JSONException {
        JSONObject jsonObject = new JSONObject(results);
        String casts = "";
        if(jsonObject != null){
            JSONArray castList = jsonObject.getJSONObject("credits").getJSONArray("cast");
            int min = 0;
            if(castList.length() < 5){
                min = castList.length();
            } else {
                min = 5;
            }
            for(int i=0; i < min;i++){
                String name = castList.getJSONObject(i).getString("name");
                casts += name + "; ";
            }
        }
        return casts;
    }

    public String getMovieDir(String results) throws JSONException {
        JSONObject jsonObject = new JSONObject(results);
        String dirs = "";
        if(jsonObject != null){
            JSONArray dirList = jsonObject.getJSONObject("credits").getJSONArray("crew");
            for(int i=0; i < dirList.length();i++){
                if(dirList.getJSONObject(i).getString("job").equals("Director")){
                    String dirName = dirList.getJSONObject(i).getString("name");
                    dirs += dirName + "; ";
                }
            }
        }
        return dirs;
    }









//    public String getCast(String keyword){
//        //deal with parameter
//        final String methodPath = "https://api.themoviedb.org/3/search/movie?api_key="+API_KEY+"&language=en-US&query="+ keyword + "&page=1&include_adult=false";
//
//        Request.Builder builder = new Request.Builder();
//        builder.url(methodPath);
//        Request request = builder.build();
//        try{
//            Response response = client.newCall(request).execute();
//            results=response.body().string();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return results;
//    }




//    public static ArrayList<String> getCasts(String results) throws JSONException {
//        JSONObject jsonObject = new JSONObject(results);
//        ArrayList<String> casts = new ArrayList<>();
//        if(jsonObject != null){
//
//            JSONArray castList = jsonObject.getJSONObject("credits").getJSONArray("cast");
//            int min = 0;
//            if(castList.length() < 5){
//                min = castList.length();
//            } else {
//                min = 5;
//            }
//            for(int i=0; i < min;i++){
//                String name = castList.getJSONObject(i).getString("name");
//                casts.add(name);
//            }
//        }
//
//        return casts;
//    }

//    public static ArrayList<String> getDirs(String results) throws JSONException {
//        JSONObject jsonObject = new JSONObject(results);
//        ArrayList<String> dirs = new ArrayList<>();
//        if(jsonObject != null){
//
//            JSONArray dirList = jsonObject.getJSONObject("credits").getJSONArray("crew");
//            for(int i=0; i < dirList.length();i++){
//                if(dirList.getJSONObject(i).getString("job").equals("Director")){
//                    dirs.add(dirList.getJSONObject(i).getString("name"));
//                }
//            }
//        }
//        return dirs;
//    }



}
