package com.example.mymoviememoir.networkconnection;

import com.example.mymoviememoir.model.MovieResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchGoogleAPI {
    private OkHttpClient client = null;
    private String results;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String API_KEY = "AIzaSyAb0tYw8d1YWMCuR-IUnPv9VXQWCA9NWLA";
    private static final String SEARCH_ID_cx = "017875931290642867302:vms0frmewn2";


    public SearchGoogleAPI(){
        client=new OkHttpClient();
    }
    private static final String BASE_URL = "https://www.googleapis.com/customsearch/";

    public String search(String keyword,String[] params, String[] values){
        //deal with parameter
        keyword = keyword.replace(" ","+");
        String query_parameter = "";
        if(params!=null && values!=null){
            for(int i=0; i < params.length;i++){
                query_parameter += "&";
                query_parameter += params[i];
                query_parameter += "=";
                query_parameter += values[i];
            }
        }

        final String methodPath = "v1?key="+API_KEY+"&cx="+ SEARCH_ID_cx + "&q="+ keyword + query_parameter;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
        }catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }

    public static ArrayList<MovieResult> getInfo(String results) throws JSONException {
        ArrayList<MovieResult> result = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(results);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            if(jsonArray != null && jsonArray.length() > 0){
                for(int index = 0; index < jsonArray.length(); index++){
                    int id = jsonArray.getJSONObject(index).getInt("id");
                    String value = jsonArray.getJSONObject(index).getString("title");
                    String name = value.substring(0,value.indexOf("("));
                    String release = value.substring(value.indexOf("(")+1,value.indexOf(")"));
                    String image = jsonArray.getJSONObject(index).getJSONObject("pagemap").getJSONArray("cse_image").getJSONObject(0).getString("src");
                    MovieResult movie = new MovieResult(id,name, release,image);
                    result.add(movie);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }


    public static String[] getDetails(String results) throws JSONException {
        String[] result = new String[2];
        try{
            JSONObject jsonObject = new JSONObject(results);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            if(jsonArray != null && jsonArray.length() > 0){
                String detail = jsonArray.getJSONObject(0).getString("snippet");
                String score = jsonArray.getJSONObject(0).getJSONObject("pagemap").getJSONArray("aggregaterating").getJSONObject(0).getString("ratingvalue");
                result[0] = detail;
                result[1] = score;
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }


}
