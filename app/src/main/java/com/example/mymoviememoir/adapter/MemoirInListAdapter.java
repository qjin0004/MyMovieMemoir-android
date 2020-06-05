package com.example.mymoviememoir.adapter;

import android.app.Activity;
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
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymoviememoir.MovieView;
import com.example.mymoviememoir.R;
import com.example.mymoviememoir.model.MovieResult;

import java.io.InputStream;
import java.util.List;

public class MemoirInListAdapter extends RecyclerView.Adapter<MemoirInListAdapter.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView movieName;
        private TextView releaseDate;
        private ImageView movieImage;
        private TextView watchDate;
        private TextView cinemaPostcode;
        private TextView comment;
        private RatingBar rating;


        public ViewHolder(View view){
            super(view);
            movieName = view.findViewById(R.id.movieName);
            releaseDate = view.findViewById(R.id.releaseDate);
            movieImage = view.findViewById(R.id.movieImage);
            watchDate = view.findViewById(R.id.watchDate);
            cinemaPostcode = view.findViewById(R.id.cinemaPostcode);
            comment = view.findViewById(R.id.comment);
            rating = view.findViewById(R.id.rating);

        }

        public final void bind(MemoirInList memoirInList, boolean isActive){
            itemView.setActivated(isActive);

            movieName.setText(memoirInList.getMovieName());
            releaseDate.setText(memoirInList.getReleaseDate());
            new MemoirInListAdapter.DownloadImageTask(movieImage).execute(memoirInList.getImage());
            //deal with watchDate format
            String date= memoirInList.getWatchDate().substring(0,memoirInList.getWatchDate().indexOf("T")) + " " + memoirInList.getWatchDate().substring(memoirInList.getWatchDate().indexOf("T") + 1, memoirInList.getWatchDate().indexOf("+"));
            watchDate.setText(date);
            cinemaPostcode.setText(String.valueOf(memoirInList.getPosition()));
            comment.setText(memoirInList.getComment());
            rating.setRating(memoirInList.getRating());
        }

        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new MemoirResultDetail(getAdapterPosition(), memoirInLists.get(getAdapterPosition()));
        }

    }

    private List<MemoirInList> memoirInLists;
    private SelectionTracker selectionTracker;

    @Override
    public int getItemCount(){
        return memoirInLists.size();
    }

    public MemoirInListAdapter(List<MemoirInList> memoirInLists){

        this.memoirInLists = memoirInLists;
    }

    public SelectionTracker getSelectionTracker() {

        return selectionTracker;
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public void addMemoir(List<MemoirInList> memoirs){
        memoirInLists = memoirs;
        notifyDataSetChanged();
    }

    @Override
    public MemoirInListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View rv_moviememoir= inflater.inflate(R.layout.rv_moviememoir,parent,false);
        ViewHolder viewHolder = new ViewHolder(rv_moviememoir);
        return viewHolder;
    }

    int selected_position = 0;
    @Override
    public void onBindViewHolder(@NonNull MemoirInListAdapter.ViewHolder viewHolder,int position){
        final MemoirInList memoirInList = memoirInLists.get(position);

//        TextView movieName = viewHolder.movieName;
//        movieName.setText(memoirInList.getMovieName());
//        TextView releaseDate = viewHolder.releaseDate;
//        releaseDate.setText(memoirInList.getReleaseDate());
//        ImageView movieImage = viewHolder.movieImage;
//        new MemoirInListAdapter.DownloadImageTask(movieImage).execute(memoirInList.getImage());
//        TextView watchDate = viewHolder.watchDate;
//        //deal with watchDate format
//        String date= memoirInList.getWatchDate().substring(0,memoirInList.getWatchDate().indexOf("T")) + " " + memoirInList.getWatchDate().substring(memoirInList.getWatchDate().indexOf("T") + 1, memoirInList.getWatchDate().indexOf("+"));
//        watchDate.setText(date);
//        TextView cinemaPostcode = viewHolder.cinemaPostcode;
//        cinemaPostcode.setText(String.valueOf(memoirInList.getPosition()));
//        TextView comment = viewHolder.comment;
//        comment.setText(memoirInList.getComment());
//        RatingBar rating = viewHolder.rating;
//        rating.setRating(memoirInList.getRating());

        viewHolder.bind(memoirInList, selectionTracker.isSelected(memoirInList));
        final int currentPosition= viewHolder.getAdapterPosition();

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentPosition== RecyclerView.NO_POSITION) return;

                notifyItemChanged(selected_position);
                selected_position = currentPosition;
                notifyItemChanged(selected_position);

                MemoirInList pass = memoirInLists.get(selected_position);

                //what to do after select
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieView.class);
                Bundle bundle = new Bundle();

                bundle.putInt("movieId",pass.getMovieId());
                bundle.putString("movieName",pass.getMovieName());
                bundle.putString("releaseDate",pass.getReleaseDate());
                bundle.putString("image",pass.getImage());


//                bundle.putParcelable("movie",moviePass);
                intent.putExtras(bundle);
                context.startActivity(intent);


            }
        });

        viewHolder.itemView.setBackgroundColor(selected_position == position ? Color.argb(255,170,224,230): Color.TRANSPARENT);

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
