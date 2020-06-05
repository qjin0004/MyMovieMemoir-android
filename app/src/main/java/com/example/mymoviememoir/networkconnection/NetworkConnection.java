package com.example.mymoviememoir.networkconnection;

import android.util.Log;
import com.example.mymoviememoir.entity.Credentials;
import com.example.mymoviememoir.entity.Person;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkConnection {
    private OkHttpClient client=null;
    private String results;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public NetworkConnection(){
        client = new OkHttpClient();
    }

    private static final String BASE_URL = "http://10.0.2.2:8080/MyMovieMemoir/webresources/";

    public Person getOneCredential(String userName, String password){
        final String methodPath = "mymoviememoir.credentials/findByUsernamePassword/" + userName + "/" + password;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        Gson g = new Gson();
        JSONObject json = null;
        Person person = null;
        try{
            Response response = client.newCall(request).execute();
            results = response.body().string();
            json = new JSONObject(results);
            results = json.getJSONObject("personId").toString();
            person = g.fromJson(results, Person.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return person;
    }


    public boolean checkExist(String userName){
        final String methodPath = "mymoviememoir.credentials/findByUsername/" + userName;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        Gson g = new Gson();
        boolean exist = true;
        try{
            Response response = client.newCall(request).execute();
            results = response.body().string();
            if(results.equals("[]")){
                exist = false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return exist;
    }


    public ArrayList<String> getAllCinemas(){
        final String methodPath = "mymoviememoir.cinema/";
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        ArrayList<String> result = new ArrayList<>();
        try{
            Response response = client.newCall(request).execute();
            results = response.body().string();
            JSONArray jsonArray = new JSONArray(results);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String cinemaName = jsonObject.getString("cinemaName");
                String postcode = String.valueOf(jsonObject.getInt("locationPostcode"));
                String cinema = cinemaName + "<" + postcode + ">";
                result.add(cinema);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

//    public ArrayList<String> getAllCinemaPostcode(){
//        ArrayList<String> result = getAllCinemas();
//        ArrayList<String> postcodes = new ArrayList<>();
//        for(int i = 0; i < result.size(); i ++){
//            String info = result.get(i);
//            postcodes.add(info.substring(info.indexOf("<")+1,info.indexOf(">")));
//        }
//        return postcodes;
//    }





    //add cinema and get the cinemaId
    public String addCinema(String cinemaName,int position){
        final String methodPath = "mymoviememoir.cinema/";
        String send = "{\"cinemaName\":\""+cinemaName+"\",\"locationPostcode\":"+position+"}";
        RequestBody body = RequestBody.create(send,JSON);
        String result = null;
        Request request = new Request.Builder().url(BASE_URL+methodPath).post(body).build();

        try{
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;

    }

    //get cinema id
    public Integer getOneCinemaId(String cinemaName, String locationPostcode){
        final String methodPath = "mymoviememoir.cinema/findByCNLP/" + cinemaName + "/" + locationPostcode;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        JSONArray jsonArray = null;
        Integer cinemaId = 0;

        try{
            Response response = client.newCall(request).execute();
            results = response.body().string();
            jsonArray = new JSONArray(results);
            cinemaId = jsonArray.getJSONObject(0).getInt("cinemaId");
        }catch(Exception e){
            e.printStackTrace();
        }
        return cinemaId;
    }


    public String addOneMemoir(String[] details){
        final String methodPath = "mymoviememoir.memoir/";
        String movieJson = "{\"cinemaId\":{\"cinemaId\":"+details[0]+"},\"comment\":\""+
                details[1]+"\",\"movieName\":\""+details[2]+"\",\"personId\":{\"personId\":"+
                details[3]+"},\"rating\":"+details[4]+",\"releaseDate\":\""+details[5]+"\",\"watchDateTime\":\""+details[6]+"\"}";
        //*** need to do, set movieJSON and then post this to backend;

        RequestBody body = RequestBody.create(movieJson,JSON);
        String result = null;
        Request request = new Request.Builder().url(BASE_URL+methodPath).post(body).build();
        try{
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public List<HashMap<String,String>> getPersonMovieMemoir(int personId){
        final String methodPath = "mymoviememoir.memoir/findByPersonId/" + personId;
        List<HashMap<String,String>> info = new ArrayList<HashMap<String,String>>();
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
            JSONArray jsonArray = new JSONArray(results);
            for(int index = 0; index < jsonArray.length(); index++) {
                HashMap<String, String> map = new HashMap<>();
                //get value from localhost
                String movieName = jsonArray.getJSONObject(index).getString("movieName");
                String releaseDate = jsonArray.getJSONObject(index).getString("releaseDate");
                String watchDateTime = jsonArray.getJSONObject(index).getString("watchDateTime");
                String locationPostcode = String.valueOf(jsonArray.getJSONObject(index).getJSONObject("cinemaId").getInt("locationPostcode"));
                String comment = jsonArray.getJSONObject(index).getString("comment");
                String rating = String.valueOf(jsonArray.getJSONObject(index).getDouble("rating"));
                // get value from api

                TheMovieDBAPI theMovieDBAPI = new TheMovieDBAPI();
                // method find id through movie name and release date
                //method find more info based id;
                int year = Integer.parseInt(releaseDate.substring(0,releaseDate.indexOf("-")));
                String[] results = theMovieDBAPI.forMovieMemoir(movieName,year);
                String id = results[0];
                String image = results[1];
                String publicRating = results[2];
                String genres = results[3];

                map.put("movieId",id);
                map.put("MovieName", movieName);
                map.put("ReleaseDate", releaseDate);
                map.put("WatchDate", watchDateTime);
                map.put("CinemaPostcode",locationPostcode);
                map.put("Comment", comment);
                map.put("SelfRating",rating);
                map.put("image",image);
                map.put("publicRating",publicRating);
                map.put("genres",genres);
                info.add(map);
            }


        } catch(Exception e){
            e.printStackTrace();
        }
        return info;
    }

    public HashMap<String,Integer> getPersonEachPostcodeMemoirNumber(int personId,String startDate,String endDate) throws ParseException {
        final String methodPath = "mymoviememoir.memoir/findByPersonId/" + personId;
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
        ArrayList<String> temp = new ArrayList<>();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
            JSONArray jsonArray = new JSONArray(results);
            for(int index = 0; index < jsonArray.length(); index++){
                String watchDateTime = jsonArray.getJSONObject(index).getString("watchDateTime");
                Date watch = new SimpleDateFormat("yyyy-MM-dd").parse(watchDateTime);
                if(watch.compareTo(start) >= 0 && end.compareTo(watch) >= 0){
                    String each = String.valueOf(jsonArray.getJSONObject(index).getJSONObject("cinemaId").getInt("locationPostcode"));
                    temp.add(each);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        HashMap<String,Integer> result = new HashMap<>();
        for(String e:temp){
            Integer count = result.get(e);
            if(count == null){
                result.put(e,1);
            } else {
                result.put(e,result.get(e)+1);
            }
        }
        return result;
    }


    //for bar chart
    public HashMap<String,Integer> getPersonEachMonthMemoirNumber(int personId,int year) {
        final String methodPath = "mymoviememoir.memoir/findByPersonId/" + personId;
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        ArrayList<String> temp = new ArrayList<>();
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
            JSONArray jsonArray = new JSONArray(results);
            for(int index = 0; index < jsonArray.length(); index++){
                String watchDateTime = jsonArray.getJSONObject(index).getString("watchDateTime");
                int watchYear = Integer.parseInt(watchDateTime.substring(0,watchDateTime.indexOf("-")));
                if(watchYear == year){
                    String watchMonth = watchDateTime.substring(5,7);
                    temp.add(watchMonth);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        HashMap<String,Integer> result = new HashMap<>();
        for(String e:temp){
            Integer count = result.get(e);
            if(count == null){
                result.put(e,1);
            } else {
                result.put(e,result.get(e)+1);
            }
        }
        return result;
    }





//    public Person getOnPersonSignIn(String userName, String password){
//        int personId = Integer.parseInt(getOneCredential(userName,password));//get personId
//        final String methodPath = "mymoviememoir.person/" + personId;
//
//        Request.Builder builder = new Request.Builder();
//        builder.url(BASE_URL + methodPath);
//        Request request = builder.build();
//
//        Gson g = new Gson();
//        Person person = null;
//        try{
//            Response response = client.newCall(request).execute();
//            results=response.body().string();
//            person = g.fromJson(results, Person.class);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        return person;
//    }




    //add a person
    public String addPerson(String[] details) {
        if(details[3].equals("")){
            details[3] = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        Person person = new Person(details[0],details[1],details[2],details[3],details[4],details[5],Integer.parseInt(details[6]));
        Gson gson = new Gson();
        String personJson = gson.toJson(person);
        String personResponse = "";
        Log.i("json",personJson);
        final String methodPath = "mymoviememoir.person/";

        RequestBody body = RequestBody.create(personJson,JSON);
        Request request = new Request.Builder().url(BASE_URL+methodPath).post(body).build();

        try{
            Response response = client.newCall(request).execute();
            personResponse = response.body().string();
        } catch(Exception e){
            e.printStackTrace();
        }
        return personResponse;
    }

    //add credential
    public String addCredential(String[] details){
        Credentials credentials = new Credentials(details[7],details[8],details[9]);
        String[] subDetails = new String[7];
        System.arraycopy(details,0,subDetails,0,subDetails.length);
        int personId = Integer.parseInt(addPerson(subDetails));
        credentials.setPersonId(personId);

        Gson gson = new Gson();
        String credentialsJson = gson.toJson(credentials);
        String credentialsResponse = "";

        Log.i("json",credentialsJson);

        final String methodPath = "mymoviememoir.credentials";

        RequestBody body = RequestBody.create(credentialsJson,JSON);
        Request request = new Request.Builder().url(BASE_URL+methodPath).post(body).build();

        try{
            Response response = client.newCall(request).execute();
            credentialsResponse = response.body().string();
            credentialsResponse = Integer.toString(personId);
        } catch(Exception e){
            e.printStackTrace();
        }
        return credentialsResponse;
    }

    //get a person
//    public Person getOnPerson(int personId){
//        final String methodPath = "mymoviememoir.person/" + personId;
//
//        Request.Builder builder = new Request.Builder();
//        builder.url(BASE_URL + methodPath);
//        Request request = builder.build();
//
//        Gson g = new Gson();
//        Person person = null;
//        try{
//            Response response = client.newCall(request).execute();
//            results=response.body().string();
//            person = g.fromJson(results, Person.class);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//        return person;
//    }



    //get a top5
    public JSONArray getOnPersonTop5(int personId){
        final String methodPath = "mymoviememoir.memoir/findTop5/" + personId;
        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();
        JSONArray jsonArray = null;
        try{
            Response response = client.newCall(request).execute();
            results=response.body().string();
            jsonArray = new JSONArray(results);
        } catch(Exception e){
            e.printStackTrace();
        }
        return jsonArray;
    }


}
