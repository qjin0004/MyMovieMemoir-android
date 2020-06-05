package com.example.mymoviememoir;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mymoviememoir.database.MovieWatchlistDatabase;
import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.model.MovieResult;
import com.example.mymoviememoir.viewmodel.MovieWatchlistViewModel;
import com.example.mymoviememoir.watchlistEntity.MovieWatchlist;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Watchlist extends Fragment {
//    private FirebaseFunctions mFunctions;
    List<HashMap<String,String>> watchlistArray;
    SimpleAdapter myListAdapter;
    ListView watchlist;
    Person person;
    //column head
    String[] colHEAD = new String[]{"MovieName","ReleaseDate","AddWatchlistDate"};
    int[] dataCell = new int[]{R.id.movieName,R.id.releaseDate,R.id.addWatchlistDate};

    Button deleteButton;
    Button viewButton;

    MovieWatchlistDatabase db = null;
    MovieWatchlistViewModel movieWatchlistViewModel;


    public Watchlist(Person person){this.person = person;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.watchlist,container,false);
        deleteButton = view.findViewById(R.id.deleteButton);
//        mFunctions = FirebaseFunctions.getInstance();
        viewButton = view.findViewById(R.id.viewButton);
        watchlist = view.findViewById(R.id.listView);
        watchlistArray = new ArrayList<>();
        //        HashMap<String,String> map = new HashMap<>();
        ////        map.put();
        //        watchlistArray.add(map);
        movieWatchlistViewModel = new ViewModelProvider(this).get(MovieWatchlistViewModel.class);
        movieWatchlistViewModel.initializeVars(getActivity().getApplication());
        movieWatchlistViewModel.getAllMovies(person.getPersonId()).observe(this,new Observer<List<MovieWatchlist>>(){
            @Override
            public void onChanged(@Nullable final List<MovieWatchlist> movieWatchlists){
                for(MovieWatchlist movie: movieWatchlists){
                    //for each piece of data can get and set to list view
                    HashMap<String,String> map = new HashMap<>();
                    map.put("MovieId",String.valueOf(movie.getId()));
                    map.put("MovieName",movie.getMovieName());
                    map.put("ReleaseDate",movie.getReleaseDate());
                    map.put("AddWatchlistDate",movie.getAddWatchlistDate());
                    watchlistArray.add(map);
                }
                myListAdapter = new SimpleAdapter(getContext(), watchlistArray,R.layout.listview_watchlist,colHEAD, dataCell);
                watchlist.setAdapter(myListAdapter);
                myListAdapter.notifyDataSetChanged();


                AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView parent,View v, final int position,long id){
                        HashMap map = (HashMap)watchlist.getItemAtPosition(position);

                        deleteButton.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v){
                                new AlertDialog.Builder(getContext()).setTitle("System reminder").setMessage("Do you want to delete this?")
                                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                movieWatchlistViewModel.delete(movieWatchlists.get(position));
                                                Toast.makeText(getContext(),movieWatchlists.get(position).getMovieName()+"delete from watchlist successful",Toast.LENGTH_SHORT).show();
                                                watchlistArray.clear();
                                                myListAdapter.notifyDataSetChanged();
                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();


                            };

                        });

                        viewButton.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                Intent intent = new Intent(getActivity(),MovieView.class);
                                Bundle bundle = new Bundle();
                                MovieWatchlist m = movieWatchlists.get(position);
                                bundle.putInt("movieId",m.getId());
                                bundle.putString("movieName",m.getMovieName());
                                bundle.putString("releaseDate",m.getReleaseDate());  // if no image, means pass from watchlist

                                bundle.putParcelable("person",person);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }
                };

                watchlist.setOnItemClickListener(listClick);
            }
        });



        return view;
    }





}
