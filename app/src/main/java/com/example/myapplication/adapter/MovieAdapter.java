package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MovieActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Movie;

import java.util.List;

import static com.example.myapplication.MainActivity.ITEMS_PER_PAGE;
import static com.example.myapplication.MainActivity.PAGE;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private boolean bool = false;

    //Constructor
    public MovieAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create view
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list,parent,false);
        MyViewHolder myViewHolderolder = new MyViewHolder(view);
        return myViewHolderolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {

            //Set basic data
            Glide.with(context).load(movieList.get(position).getBackdropPath()).into(holder.imageView);
            holder.title.setText(movieList.get(position).getTitle());
            holder.rating.setText(Double.toString(movieList.get(position).getVoteAverage()));
            holder.release.setText(movieList.get(position).getReleaseDate());

            //Button for star on/off
            holder.btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(!bool){
                        bool = true;
                        holder.btn.setBackgroundResource(R.drawable.ic_star_golden);
                    }
                    else{
                        bool = false;
                        holder.btn.setBackgroundResource(R.drawable.ic_star);
                    }
                }
            });

            final int currentMovie = PAGE*ITEMS_PER_PAGE+position;

            //Button for content if you click open new activity
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    try {
                        //Toast.makeText(context,Integer.toString(currentMovie),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context,MovieActivity.class);
                        intent.putExtra("position",currentMovie);
                        context.startActivity(intent);
                    }catch (Exception r){
                        Log.e("Error: ", r.getMessage());
                    }
                    //Toast.makeText(context,"Clicked: " + movieList.get(position).getTitle(),Toast.LENGTH_LONG).show();
                }
            });
        }catch(Exception e){
            Log.e("Error: ", "Into onBindViewHolder and " + e.getMessage() );
        }

    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title;
        TextView rating;
        TextView release;
        Button btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = (Button)itemView.findViewById(R.id.star_rating);
            imageView = (ImageView) itemView.findViewById(R.id.poster);
            title = (TextView) itemView.findViewById(R.id.title);
            release = (TextView) itemView.findViewById(R.id.release);
            rating = (TextView) itemView.findViewById(R.id.rating);

        }
    }
}
