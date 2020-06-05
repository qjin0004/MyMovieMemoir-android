package com.example.mymoviememoir;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mymoviememoir.database.MovieWatchlistDatabase;
import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.model.MovieResult;
import com.example.mymoviememoir.networkconnection.TheMovieDBAPI;
import com.example.mymoviememoir.viewmodel.MovieWatchlistViewModel;
import com.example.mymoviememoir.watchlistEntity.MovieWatchlist;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import org.json.JSONException;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MovieView extends AppCompatActivity {
    MovieWatchlistDatabase db = null;
    TextView movieName;
    TextView releaseDate;
    RatingBar rating;
    TextView genre;
    ImageView movieImage;
    TextView storyLine;
    TextView director;
    TextView cast;
    TextView country;
    TheMovieDBAPI theMovieDBAPI = null;
    Button addWatchlistButton;
    Button addMemoirButton;
    Button addWatchlistFirebaseButton;
    MovieWatchlistViewModel movieWatchlistViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movieview);
        theMovieDBAPI = new TheMovieDBAPI();

        country = findViewById(R.id.country);
        genre = findViewById(R.id.genre);
        movieName = findViewById(R.id.movieName);
        releaseDate = findViewById(R.id.releaseDate);
        movieImage = findViewById(R.id.movieImage);
        storyLine = findViewById(R.id.storyLine);
        rating = findViewById(R.id.rating);
        director = findViewById(R.id.director);
        cast = findViewById(R.id.cast);
        addMemoirButton = findViewById(R.id.addMemoirButton);
        addWatchlistButton = findViewById(R.id.addWatchlistButton);
        addWatchlistFirebaseButton = findViewById(R.id.addWatchlistFirebaseButton);
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        final int movieId = bundle.getInt("movieId");
//        final MovieResult selectMovie = bundle.getParcelable("movie");  //pass from movieSearch page;

        final Person person = bundle.getParcelable("person");
        //?
        final String name = bundle.getString("movieName");
        movieName.setText(name);
        final String release = bundle.getString("releaseDate");
        releaseDate.setText(release);
        final String image = bundle.getString("image");   // if no image, means pass from watchlist
        new DownloadImageTask(movieImage).execute(image);




        if(person == null){
            addMemoirButton.setVisibility(View.INVISIBLE);
            addWatchlistButton.setVisibility(View.INVISIBLE);
        }

        //接受传输 判断显示
        movieWatchlistViewModel = new ViewModelProvider(this).get(MovieWatchlistViewModel.class);
        movieWatchlistViewModel.initializeVars(getApplication());
        //do validation
        if( person != null){ //-> from movie search
            movieWatchlistViewModel.getAllMovies(person.getPersonId()).observe(this, new Observer<List<MovieWatchlist>>() {
                @Override
                public void onChanged(@Nullable final List<MovieWatchlist> movieList){
                    //判断展示的是什么电影entity
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    final MovieWatchlist movie = new MovieWatchlist(person.getPersonId(),movieId,name,release,currentDate);
                    boolean pass = true;
                    for (MovieWatchlist one : movieList) {
                        if(one.getId() == movie.getId()){
                            pass = false;
                            addWatchlistButton.setEnabled(false);
//                            Toast.makeText(getApplicationContext(),movie.getMovieName()+" is existed in watchlist ~",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    //add
                    addWatchlistButton.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            // on click for share in social media
                            //additional choices:
                                movieWatchlistViewModel.insert(movie);

                                Toast.makeText(getApplicationContext(),"add " + movie.getMovieName()+" in watchlist successfully",Toast.LENGTH_SHORT).show();
                                addWatchlistButton.setEnabled(false);

                        }
                    });

                    addWatchlistFirebaseButton.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            // do more
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myDatabase =  database.getReference();
                            Toast.makeText(getApplicationContext(),"add " + movie.getMovieName()+" in watchlist successfully",Toast.LENGTH_SHORT).show();
                            addWatchlistFirebaseButton.setEnabled(false);
                            myDatabase.child("watchlist").child(String.valueOf(person.getPersonId())).child(String.valueOf(movieId)).setValue(movie);

                        }
                    });
                }
            });
        }

        //接受传输 判断显示
//        final MovieResult movieWatchlist = bundle.getParcelable("movieWatchlist");
//        if(movieWatchlist!=null){
//            addWatchlistButton.setEnabled(false);
//            String name = movieWatchlist.getName();
//            movieName.setText(name);
//            String release = movieWatchlist.getReleaseDate();
//            releaseDate.setText(release);
//        }


        //load information
        new AsyncTask<String,Void,String[]>(){
            @Override
            protected String[] doInBackground(String... strings) {
                String result = theMovieDBAPI.movieIdSearch(movieId);
                String castDir = theMovieDBAPI.getCastDirInfoByID(movieId);
                String[] results = new String[]{result,castDir};
                return results;
            }

            @Override
            protected void onPostExecute(String[] results){
                //load info from backend
                //除了cast/dir剩下都是从第一个结果获取到的
                try {
                    //country
                    country.setText(theMovieDBAPI.getCountryById(results[0]));
                    //image
                    final String imageURL = theMovieDBAPI.getImageById(results[0]);
                    new DownloadImageTask(movieImage).execute(imageURL);
                    //rating
                    float score = Float.parseFloat(theMovieDBAPI.getPublilcRatingById(results[0]))/10*5;
                    rating.setRating(score);
                    //story
                    storyLine.setText(theMovieDBAPI.getShortStoryById(results[0]));
                    //genres
                    genre.setText(theMovieDBAPI.getGenresById(results[0]));
                    //cast
                    cast.setText(theMovieDBAPI.getMovieTop5Casts(results[1]));
                    //dir
                    director.setText(theMovieDBAPI.getMovieDir(results[1]));


                    addMemoirButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){

                            //combine
                            MovieResult pass = new MovieResult(movieId,name,release,imageURL);


                            Intent intent = new Intent(MovieView.this,AddMemoir.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("movie",pass);
                            bundle.putParcelable("person",person);
                            intent.putExtras (bundle);
                            startActivity(intent);
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.execute();



        //add memoir


    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
