package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.model.Movie;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.MainActivity.movies;

public class MovieActivity extends AppCompatActivity {

    int position = -1;
    private List<Movie> movieList = new ArrayList<>();
    private Context context = MovieActivity.this;

    private ImageView imageView, back;
    private TextView release, rating, title, overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        init();
        getIntentMethod();

        //Set data
        try {
            rating.setText(Double.toString(movieList.get(position).getVoteAverage()));
            release.setText(movieList.get(position).getReleaseDate());
            title.setText(movieList.get(position).getTitle());
            overview.setText(movieList.get(position).getOverview());
            Glide.with(context).load(movieList.get(position).getBackdropPath()).into(imageView);
        }catch (Exception t){
            Log.e("Error here:", t.getMessage());
        }

        //Button back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    //Init all btn text image view
    private void init(){
        imageView = (ImageView)findViewById(R.id.poster);
        rating = (TextView)findViewById(R.id.rating);
        release = (TextView)findViewById(R.id.release);
        title = (TextView)findViewById(R.id.title);
        overview = (TextView)findViewById(R.id.content);
        back = (ImageView)findViewById(R.id.back_btn);
    }

    //Get position from adapter and load Array
    private void getIntentMethod(){
        try{
            position = getIntent().getIntExtra("position", -1);
            movieList = movies;
        }catch (Exception e){
            Log.e("Error: ", "Movie Activity, getIntentMethod" + e.getMessage());
        }
    }
}
