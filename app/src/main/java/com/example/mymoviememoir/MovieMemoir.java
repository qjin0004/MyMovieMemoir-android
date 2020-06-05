package com.example.mymoviememoir;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviememoir.adapter.ActionModeController;
import com.example.mymoviememoir.adapter.MemoirInList;
import com.example.mymoviememoir.adapter.MemoirInListAdapter;
import com.example.mymoviememoir.adapter.MemoirKeyProvider;
import com.example.mymoviememoir.adapter.MovieResultLookup;
import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.model.MovieResult;
import com.example.mymoviememoir.networkconnection.NetworkConnection;
import com.example.mymoviememoir.networkconnection.TheMovieDBAPI;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MovieMemoir extends Fragment {
    NetworkConnection networkConnection;
    TheMovieDBAPI theMovieDBAPI;
    SelectionTracker selectionTracker;
    Person person;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<MemoirInList> memoirs;
    private MemoirInListAdapter adapter;
    private ActionMode actionMode;
    private Spinner sort;
    private Spinner filter;

    public MovieMemoir(Person person){
        this.person = person;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        networkConnection = new NetworkConnection();
        theMovieDBAPI = new TheMovieDBAPI();
        View view = inflater.inflate(R.layout.moviememoir, container, false);

        memoirs = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        sort = view.findViewById(R.id.cinemaSP);
        filter = view.findViewById(R.id.genreSp);
        adapter = new MemoirInListAdapter(memoirs);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        GetAllMemoirTask getAllMemoirTask = new GetAllMemoirTask();
        getAllMemoirTask.execute();

        //shower filter bar
        new AsyncTask<Void,Void,List<String>>(){
            @Override
            protected List<String> doInBackground(Void... params){
                List<String> result = new ArrayList<>();
                try {
                    result.add("All");
                    List<String> generlist =theMovieDBAPI.dealFilterItem();
                    for(String gener:generlist){
                        result.add(gener);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }
            @Override
            protected void onPostExecute(List<String> result){
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,result);
                filter.setAdapter(spinnerAdapter);
            }
        }.execute();




        //set spinner

        selectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MemoirKeyProvider(1, memoirs),
                new MovieResultLookup(recyclerView),
                StorageStrategy.createLongStorage()
        ).build();

        adapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onItemStateChanged(Object key, boolean selected) {
                super.onItemStateChanged(key, selected);
            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(new ActionModeController(getContext(), selectionTracker));
                    //setMenuItemTitle(selectionTracker.getSelection().size());

                }
                else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }

                Iterator<MovieResult> itemIterable = selectionTracker.getSelection().iterator();
                while (itemIterable.hasNext()) {
                    //Log.i(itemIterable.next().getName());

                }
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }

        });


        //execute filter function
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<MemoirInList> temp = new ArrayList<>();
                String filterItem = parent.getItemAtPosition(position).toString();
                if(filterItem.equals("All")){
                    //do all
                    temp = memoirs;
                } else {
                    //condition
                    for(int i = 0; i < memoirs.size(); i++){
                        String[] genres = memoirs.get(i).getGenres();
//                    System.out.println("test index of list: "+i);
                        for(int index = 0; index < genres.length; index++){
                            if(genres[index].equals(filterItem)){
                                temp.add(memoirs.get(i));
                            }
                        }
                    }
                }
                //finish

                adapter.addMemoir(temp);
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        //get spinner value
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortItem = parent.getItemAtPosition(position).toString();
                //condition
                if(sortItem.equalsIgnoreCase("Watch Date")){
                    Collections.sort(memoirs,new Comparator<MemoirInList>(){
                        @Override
                        public int compare(MemoirInList lhs, MemoirInList rhs){
                            int result = 0;
                            try {
                                Date lhsDate = new SimpleDateFormat("yyyy-MM-dd").parse(lhs.getWatchDate());
                                Date rhsDate = new SimpleDateFormat("yyyy-MM-dd").parse(rhs.getWatchDate());
                                if(lhsDate.compareTo(rhsDate) > 0){
                                    result = -1;
                                } else if(lhsDate.compareTo(rhsDate) < 0){
                                    result = 1;
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return result;
                        }
                    });
                } else if(sortItem.equalsIgnoreCase("Rating Score")){
                    Collections.sort(memoirs,new Comparator<MemoirInList>(){
                        @Override
                        public int compare(MemoirInList lhs, MemoirInList rhs){
                            int result = 0;
                            if(lhs.getRating()>rhs.getRating()){
                                result = -1;
                            } else if(lhs.getRating()<rhs.getRating()){
                                result = 1;
                            }
                            return result;
                        }
                    });
                } else if (sortItem.equalsIgnoreCase("Public Rating")){
                    Collections.sort(memoirs,new Comparator<MemoirInList>(){
                        @Override
                        public int compare(MemoirInList lhs, MemoirInList rhs){
                            int result = 0;
                            if(lhs.getPublicRating()>rhs.getPublicRating()){
                                System.out.println("lhs:" + lhs.getMovieName() + ";rhs: "+rhs.getMovieName());
                                System.out.println("lhs:" + lhs.getPublicRating() + ";rhs: "+rhs.getPublicRating());
                                result = -1;
                            } else if(lhs.getPublicRating()<rhs.getPublicRating()){
                                System.out.println("lhs:" + lhs.getMovieName() + ";rhs: "+rhs.getMovieName());
                                System.out.println("lhs:" + lhs.getPublicRating() + ";rhs: "+rhs.getPublicRating());
                                result = 1;
                            }
                            return result;
                        }
                    });
                }
                adapter.addMemoir(memoirs);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        return view;
    }

    private void saveData(final List<HashMap<String,String>> results){
        for(int index = 0; index<results.size(); index++){
            // request id based name and year, request info based id.
            String MovieName = results.get(index).get("MovieName");
            int movieId = Integer.parseInt(results.get(index).get("movieId"));
            String ReleaseDate = results.get(index).get("ReleaseDate");
            String WatchDate = results.get(index).get("WatchDate");
            String image = results.get(index).get("image");
            int CinemaPostcode = Integer.parseInt(results.get(index).get("CinemaPostcode"));
            String Comment = results.get(index).get("Comment");
            float SelfRating = Float.parseFloat(results.get(index).get("SelfRating"));
            float publicRating = Float.parseFloat(results.get(index).get("publicRating"))/2;
            String temp = results.get(index).get("genres");
            String[] genres = temp.split("; ");
            System.out.println("***Name: "+ MovieName + ";Self Rating: "+ SelfRating + "; Public Rating: "+ publicRating);
            MemoirInList memoirInList = new MemoirInList(movieId,MovieName,ReleaseDate,image,WatchDate,CinemaPostcode,Comment,SelfRating,publicRating,genres);

            memoirs.add(memoirInList);

            for(int i= 0; i < memoirs.size(); i++){
                System.out.println("Name: "+ memoirs.get(i).getMovieName() + ";Self Rating: "+ memoirs.get(i).getRating() + "; Public Rating: "+ memoirs.get(i).getPublicRating());
            }
        }

        //default sort -> watch date
        Collections.sort(memoirs,new Comparator<MemoirInList>(){
            @Override
            public int compare(MemoirInList lhs, MemoirInList rhs){
                int result = 0;
                try {
                    Date lhsDate = new SimpleDateFormat("yyyy-MM-dd").parse(lhs.getWatchDate());
                    Date rhsDate = new SimpleDateFormat("yyyy-MM-dd").parse(rhs.getWatchDate());
                    if(lhsDate.compareTo(rhsDate) > 0){
                        result = -1;
                    } else if(lhsDate.compareTo(rhsDate) < 0){
                        result = 1;
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return result;
            }
        });


        adapter.addMemoir(memoirs);

    }

    private class GetAllMemoirTask extends AsyncTask<String,Void,List<HashMap<String,String>>>{
        final ProgressDialog dialog = new ProgressDialog(getContext());
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog.setMessage("Loading");
            dialog.show();
        }

        @Override
        protected List<HashMap<String,String>> doInBackground(String... params){
            int personId =  person.getPersonId();
            return networkConnection.getPersonMovieMemoir(personId);
        }
        @Override
        protected void onPostExecute(List<HashMap<String,String>> result){
            saveData(result);
            if(dialog.isShowing()){
                dialog.dismiss();
            }
        }

    }



}
