package com.example.mymoviememoir.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviememoir.MovieSearch;
import com.example.mymoviememoir.MovieView;
import com.example.mymoviememoir.R;
import com.example.mymoviememoir.SignUp;
import com.example.mymoviememoir.entity.Person;
import com.example.mymoviememoir.model.MovieResult;

import java.io.InputStream;
import java.util.List;

public class MovieSearchRecyclerViewAdapter extends RecyclerView.Adapter <MovieSearchRecyclerViewAdapter.ViewHolder> {
    private List<MovieResult> movieResults;
    private SelectionTracker selectionTracker;
    private Person person;

    public class ViewHolder extends RecyclerView.ViewHolder implements ViewHolderWithDetails{
        public TextView movieName;
        public TextView releaseYear;
        public ImageView movieImage;


        public ViewHolder(View itemView){
            super(itemView);
            movieName = itemView.findViewById(R.id.movieName);
            releaseYear = itemView.findViewById(R.id.releaseYear);
            movieImage = itemView.findViewById(R.id.movieImage);
//            context = itemView.getContext();
        }

        public final void bind(MovieResult movieResult, boolean isActive) {
            itemView.setActivated(isActive);
            new DownloadImageTask(movieImage).execute(movieResult.getImage());
            movieName.setText(movieResult.getName());
            releaseYear.setText(movieResult.getReleaseDate());
        }

        @Override
        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new MovieResultDetail(getAdapterPosition(), movieResults.get(getAdapterPosition()));
        }
    }


    public MovieSearchRecyclerViewAdapter(Person person, List<MovieResult> movieResults){
        this.person = person;
        this.movieResults = movieResults;
    }

    public SelectionTracker getSelectionTracker() {
        return selectionTracker;
    }
    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @Override
    public ViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View moviesView = inflater.inflate(R.layout.rv_moviesearch,parent,false);
        ViewHolder viewHolder = new ViewHolder(moviesView);
        return viewHolder;
    }

    int selected_position = 0;
    @Override
    public void onBindViewHolder(@NonNull MovieSearchRecyclerViewAdapter.ViewHolder viewHolder, int position){
        final MovieResult movie = movieResults.get(position);

        viewHolder.bind(movie,selectionTracker.isSelected(movie));

        final int currentPosition = viewHolder.getAdapterPosition();


        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // what to do
                //use API get details
                if(currentPosition==RecyclerView.NO_POSITION) return;
                notifyItemChanged(selected_position);
                selected_position = currentPosition;


                notifyItemChanged(selected_position);
//                Log.i(null,String.valueOf(movieResults.get(selected_position)));
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieView.class);
                Bundle bundle = new Bundle();
                MovieResult pass = movieResults.get(selected_position);
                //test
//                bundle.putParcelable("movie",pass);
                bundle.putInt("movieId",pass.getMovieId());
                bundle.putString("movieName",pass.getName());
                bundle.putString("releaseDate",pass.getReleaseDate());
                bundle.putString("image",pass.getImage());

                bundle.putParcelable("person",person);
                intent.putExtras(bundle);
                context.startActivity(intent);

            }
        });
        viewHolder.itemView.setBackgroundColor(selected_position == position ? Color.argb(255,170,224,230): Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return movieResults.size();
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


//    protected void sendToast(String msg){
//        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
//    }
}
