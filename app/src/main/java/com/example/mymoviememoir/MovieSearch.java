package com.example.mymoviememoir;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviememoir.adapter.ActionModeController;
import com.example.mymoviememoir.adapter.MovieKeyProvider;
import com.example.mymoviememoir.adapter.MovieResultLookup;
import com.example.mymoviememoir.adapter.MovieSearchRecyclerViewAdapter;
import com.example.mymoviememoir.adapter.Predicate;
import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.model.MovieResult;
//import com.example.mymoviememoir.networkconnection.SearchGoogleAPI;
import com.example.mymoviememoir.networkconnection.TheMovieDBAPI;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MovieSearch extends Fragment {
    private static final String TAG = "MovieSearch";
    final Person person;
    TheMovieDBAPI theMovieDBAPI;
    SelectionTracker selectionTracker;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private EditText etSearchMovie;
    private Button searchButton;
    private List<MovieResult> movieResults;
    private MovieSearchRecyclerViewAdapter adapter;
    private ActionMode actionMode;

    public MovieSearch(Person person){this.person = person;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.moviesearch,container, false);
        theMovieDBAPI = new TheMovieDBAPI();

        recyclerView = view.findViewById(R.id.recyclerView);

        movieResults = new ArrayList<MovieResult>();


        etSearchMovie = view.findViewById(R.id.etSearchMovie);
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String movieName = etSearchMovie.getText().toString().trim();
                if(!movieName.isEmpty()){
                    // to be parameter search on API
                    new AsyncTask<String, Void, String>(){
                        @Override
                        protected String doInBackground(String... params) {
                            return theMovieDBAPI.search(movieName);
                        }


                        @Override
                        protected void onPostExecute(String result){
                            try {
                                movieResults =  theMovieDBAPI.getInfo(result);
                                adapter = new MovieSearchRecyclerViewAdapter(person,movieResults);  //add the list to adapter
                                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                                recyclerView.setAdapter(adapter);

                                layoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(layoutManager);

                                selectionTracker = new SelectionTracker.Builder<>(
                                        "my-selection-id", recyclerView,
                                        new MovieKeyProvider(1, movieResults),
                                        new MovieResultLookup(recyclerView),
                                        StorageStrategy.createLongStorage()
                                ).withSelectionPredicate(new Predicate()).build();

                                adapter.setSelectionTracker(selectionTracker);
                                selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
                                    @Override
                                    public void onItemStateChanged(@NonNull Object key, boolean selected) {
                                        super.onItemStateChanged(key, selected);
                                        // reference to the movie view

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
                                            Log.i(TAG,itemIterable.next().getName());

                                        }
                                    }

                                    @Override
                                    public void onSelectionRestored() {
                                        super.onSelectionRestored();
                                    }

                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute();

                }
            }

        });
        return view;
    }




    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
        selectionTracker.onSaveInstanceState(outState);
    }

    protected void sendToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
    }
}
