package com.example.mymoviememoir;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.model.MovieResult;
import com.example.mymoviememoir.networkconnection.NetworkConnection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddMemoir extends AppCompatActivity {
    NetworkConnection networkConnection = null;
    TextView movieName;
    TextView releaseDate;
    EditText editWatchDate;
    EditText editWatchTime;
    DatePickerDialog picker;
    TimePickerDialog timePickerDialog;
    Button addCinemaButton;
    EditText etAddCinema;
    EditText etComment;
    ImageView movieImage;
    Button submitButton;
    Person person;
    RatingBar ratingBar;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmemoir);
        networkConnection = new NetworkConnection();

        Bundle bundle = getIntent().getExtras();
        final MovieResult movie = bundle.getParcelable("movie");
        person = bundle.getParcelable("person");
        //set movie name
        movieName = findViewById(R.id.movieName);
        movieName.setText(movie.getName());
        //set release date
        releaseDate = findViewById(R.id.releaseDate);
        releaseDate.setText(movie.getReleaseDate());
        //set image
        movieImage = findViewById(R.id.movieImage);
        new DownloadImageTask(movieImage).execute(movie.getImage());




        //set date
        editWatchDate = findViewById(R.id.editWatchDate);
        editWatchDate.setInputType(InputType.TYPE_NULL);
        editWatchDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                picker = new DatePickerDialog(AddMemoir.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String mon = null;
                        if((month + 1) < 10){
                            mon = "0" + (month + 1);
                        }else {
                            mon = String.valueOf((month + 1));
                        }
                        String date = null;
                        if(dayOfMonth < 10){
                            date = "0" + dayOfMonth;
                        } else {
                            date = String.valueOf(dayOfMonth);
                        }

                        editWatchDate.setText(year + "-" + mon + "-" + date);
                    }
                },year,month,day);
                picker.show();
            }
        });


        //set time
        editWatchTime = findViewById(R.id.editWatchTime);
        editWatchTime.setInputType(InputType.TYPE_NULL);
        editWatchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(AddMemoir.this, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view,int hourOfDay,int minute){

                        String hou = null;
                        if(hourOfDay < 10){
                            hou = "0" + hourOfDay;
                        }else {
                            hou = String.valueOf(hourOfDay);
                        }
                        String min = null;
                        if(minute < 10){
                            min = "0" + minute;
                        } else {
                            min = String.valueOf(minute);
                        }



                        editWatchTime.setText(hou + ":" + min);
                    }
                },mHour,mMinute,false);
                timePickerDialog.show();
            }
        });




        //set cinema spinner
        final Spinner cinemaSP = findViewById(R.id.cinemaSP);

        addCinemaButton = findViewById(R.id.addCinemaButton);
        etAddCinema = findViewById(R.id.etAddCinema);
        new AsyncTask<Void,Void,ArrayList<String>>() {
            @Override
            protected ArrayList<String> doInBackground(Void... params) {
                return networkConnection.getAllCinemas();
            }

            @Override
            protected void onPostExecute(ArrayList<String> results) {
                final List<String> cinemaDetails = results;
                final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,cinemaDetails);
                cinemaSP.setAdapter(spinnerAdapter);


                addCinemaButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cinemaInfo = etAddCinema.getText().toString().trim();
                        if(!cinemaInfo.equals((""))){
                            spinnerAdapter.add(cinemaInfo);
                            spinnerAdapter.notifyDataSetChanged();
                            String movieName = cinemaInfo.substring(0,cinemaInfo.indexOf("<"));
                            String postcode = cinemaInfo.substring(cinemaInfo.indexOf("<")+1,cinemaInfo.indexOf(">"));
                            AddCinemaTask addCinemaTask = new AddCinemaTask();
                            addCinemaTask.execute(movieName,postcode);
                        }

                    }
                });

            }


        }.execute();

        //comment;
        etComment = findViewById(R.id.etComment);
        //set rating
        Button ratingButton = findViewById(R.id.ratingButton);
        ratingBar = new RatingBar(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);
        ratingBar.setLayoutParams(layoutParams);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize((float) 0.5);

        LinearLayout ratingLayout = findViewById(R.id.ratingLayout);
        if(ratingLayout != null){
            ratingLayout.addView(ratingBar);
        }
        if(ratingButton != null){
            ratingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = String.valueOf(ratingBar.getRating());
                    Toast.makeText(AddMemoir.this,message,Toast.LENGTH_SHORT).show();
                }
            });
        }


        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cinemaInfo = cinemaSP.getSelectedItem().toString(); // to get cinemaId
                String cinemaName = cinemaInfo.substring(0,cinemaInfo.indexOf("<"));
                String postcode = cinemaInfo.substring(cinemaInfo.indexOf("<")+1,cinemaInfo.indexOf(">"));

                GetOneCinemaId getOneCinemaId = new GetOneCinemaId();
                getOneCinemaId.execute(cinemaName,postcode);
            }
        });



    }

    private class AddCinemaTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params){
            String cinema = params[0];
            int postcode = Integer.parseInt(params[1]);
            return networkConnection.addCinema(cinema,postcode);

        }
        @Override
        protected void onPostExecute(String message){
            String cinemaId = message;
        }

    }

    private class GetOneCinemaId extends AsyncTask<String,Void,Integer>{
        @Override
        protected Integer doInBackground(String... params){
            return networkConnection.getOneCinemaId(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(Integer result){
            final String cinemaId = String.valueOf(result);
            String comment = etComment.getText().toString();
            String rating = String.valueOf(ratingBar.getRating());
            String release = releaseDate.getText().toString();
            String name = movieName.getText().toString();
            String personId = String.valueOf(person.getPersonId());
            String watchDate = editWatchDate.getText().toString();
            String watchTime = editWatchTime.getText().toString();
            String watchDateTime = watchDate+"T"+watchTime+":00+11:00";
            final String[] details = new String[]{cinemaId,comment,name,personId,rating,release,watchDateTime};
            new AsyncTask<String,Void,String>() {
                @Override
                protected String doInBackground(String... params) {
                    return networkConnection.addOneMemoir(details);
                }

                @Override
                protected void onPostExecute(String results) {
                    Toast.makeText(AddMemoir.this,"Add to your memoir list",Toast.LENGTH_SHORT).show();
                }
            }.execute();
        }
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
