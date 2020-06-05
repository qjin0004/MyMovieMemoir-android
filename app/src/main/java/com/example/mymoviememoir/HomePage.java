package com.example.mymoviememoir;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.networkconnection.NetworkConnection;
import com.example.mymoviememoir.networkconnection.TheMovieDBAPI;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomePage extends Fragment{
    Person person;
    NetworkConnection networkConnection = null;
    //match listView
    List<HashMap<String,String>> movieTop5Array;
    SimpleAdapter myListAdapter;
    ListView moviesTop5;
    //column head
    String[] colHEAD = new String[]{"Movie Name","Release Date","Rating Score"};
    int[] dataCell = new int[]{R.id.movieName,R.id.releaseDate,R.id.ratingScore}; //store view id

    public HomePage(Person person){
        this.person = person;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.homepage, container,false);
        networkConnection = new NetworkConnection();

        //接收person
        int personId = person.getPersonId();
        String firstName = person.getFirstName();

        TextView firstNameTV = view.findViewById(R.id.firstName);
        firstNameTV.setText(firstName);
        TextView currentDate = view.findViewById(R.id.currentDate);
        currentDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        //set an image
        ImageView myImage = view.findViewById(R.id.homePicture);
        myImage.setImageResource(R.drawable.homepage);

        //define list view
        moviesTop5 = view.findViewById(R.id.homeListTop5);
        movieTop5Array = new ArrayList<>();
        HashMap<String,String> map = new HashMap<>();


        ReturnPersonTop5Task returnPersonTop5Task = new ReturnPersonTop5Task();
        returnPersonTop5Task.execute(personId);

        myListAdapter = new SimpleAdapter(getContext(),movieTop5Array,R.layout.homelisttop5,colHEAD,dataCell);
        moviesTop5.setAdapter(myListAdapter);


        return view;
    }



    private class ReturnPersonTop5Task extends AsyncTask<Integer,Void,JSONArray>{
        @Override
        protected JSONArray doInBackground(Integer... params){
            int id = params[0].intValue();
            return networkConnection.getOnPersonTop5(id);
        }

        @Override
        protected void onPostExecute(JSONArray movies){
            for(int index = 0; index < movies.length(); index++){
                HashMap<String,String> map = new HashMap<>();
                try {
                    map.put("Movie Name",movies.getJSONObject(index).getString("movieName"));
                    map.put("Release Date",movies.getJSONObject(index).getString("realease year"));
                    map.put("Rating Score",movies.getJSONObject(index).getString("rating"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addMap(map);
            }
        }
    }

    protected void addMap(HashMap map){
        movieTop5Array.add(map);
        myListAdapter = new SimpleAdapter(getContext(), movieTop5Array,R.layout.homelisttop5,colHEAD, dataCell);
        moviesTop5.setAdapter(myListAdapter);
    }




}
