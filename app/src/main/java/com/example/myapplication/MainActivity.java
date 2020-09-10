package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.MovieAdapter;
import com.example.myapplication.api.Client;
import com.example.myapplication.api.Service;
import com.example.myapplication.model.Movie;
import com.example.myapplication.model.ResponseMovie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //View
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MovieAdapter adapter;
    static List<Movie> movies = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    //Page function
    public static int PAGE = 0;
    private int TOTAL_NUM_ITEMS;
    public static int ITEMS_PER_PAGE=7;
    private int ITEMS_REMAINING;
    private int LAST_PAGE;
    private Button nextBtn, prevBtn;
    private TextView numPage;
    //Internet
    static boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextBtn = (Button) findViewById(R.id.next);
        prevBtn = (Button) findViewById(R.id.prev);
        numPage = (TextView) findViewById(R.id.id_page);

        initView();

        //swipe
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.main);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                initView();
                Toast.makeText(MainActivity.this,"Movie Refreshed", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Click for navigation next/prev page
        try {
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PAGE += 1;
                    if(PAGE > LAST_PAGE){
                        //Toast.makeText(MainActivity.this,Integer.toString(page),Toast.LENGTH_LONG).show();
                        PAGE = LAST_PAGE;
                    }
                    //Toast.makeText(MainActivity.this,Integer.toString(page) + " - " + Integer.toString(LAST_PAGE),Toast.LENGTH_LONG).show();
                    recyclerView.setAdapter(new MovieAdapter(pagePos(PAGE), MainActivity.this));
                    numPage.setText(Integer.toString(PAGE+1));
                }
            });
            prevBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PAGE -= 1;
                    if(PAGE < 1){
                        //Toast.makeText(MainActivity.this,Integer.toString(page),Toast.LENGTH_LONG).show();
                        PAGE = 0;
                    }
                    recyclerView.setAdapter(new MovieAdapter(pagePos(PAGE), MainActivity.this));
                    numPage.setText(Integer.toString(PAGE+1));
                }
            });
        }catch(Exception e){
            Log.e("Error", "Here: " + e.getMessage());
        }

    }

    private void initView(){
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        checkInternetConnection();
        //if connection true get Information from Internet with API
        if(isConnected) {
            Service service = Client.getClient().create(Service.class);
            Call<ResponseMovie> call = service.getPopular(Constant.API_KEY);
            call.enqueue(new Callback<ResponseMovie>() {
                @Override
                public void onResponse(Call<ResponseMovie> call, Response<ResponseMovie> response) {
                    //Successful
                    //load into Array all data from Web Content(API)
                    movies = response.body().getMovie();
                    saveData();
                    TOTAL_NUM_ITEMS = movies.size();
                    ITEMS_REMAINING = TOTAL_NUM_ITEMS % ITEMS_PER_PAGE;
                    LAST_PAGE = TOTAL_NUM_ITEMS / ITEMS_PER_PAGE;
                    adapter = new MovieAdapter(pagePos(PAGE), MainActivity.this);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onFailure(Call<ResponseMovie> call, Throwable t) {
                    //Failed
                }
            });
        }
        //if connection false load from cache
        else{
            try {
                loadData();
                TOTAL_NUM_ITEMS = movies.size();
                ITEMS_REMAINING = TOTAL_NUM_ITEMS % ITEMS_PER_PAGE;
                LAST_PAGE = TOTAL_NUM_ITEMS / ITEMS_PER_PAGE;
                adapter = new MovieAdapter(pagePos(PAGE), MainActivity.this);
                recyclerView.setAdapter(adapter);
            }catch(Exception e){
                Log.e("Error Here",e.getMessage());
            }
        }
        numPage.setText(Integer.toString(PAGE+1));
    }

    //Save Data
    private void saveData() {
        Toast.makeText(MainActivity.this,"Save Data on cache" + isConnected,Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(movies);
        editor.putString("movie list", json);
        editor.apply();
    }

    //Load Data
    private void loadData() {
        Toast.makeText(MainActivity.this,"Load Data from cache",Toast.LENGTH_LONG).show();
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("movie list", null);
        Type type = new TypeToken<ArrayList<Movie>>() {}.getType();
        movies = gson.fromJson(json, type);
        if (movies == null) {
            movies = new ArrayList<>();
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);

        }
    }

    //Check internet connection
    public void checkInternetConnection(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                Toast.makeText(MainActivity.this,"WiFi " + isConnected,Toast.LENGTH_LONG).show();
            }
            else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                Toast.makeText(MainActivity.this,"Mobile Internet",Toast.LENGTH_LONG).show();
            }
        }
        else {
            isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            Toast.makeText(MainActivity.this,"Not internet " + isConnected,Toast.LENGTH_LONG).show();
        }

    }

    //Page logic
    private List<Movie> pagePos(int pos){
        int start = pos*ITEMS_PER_PAGE;
        int numOfData = ITEMS_PER_PAGE;
        List<Movie> movie = new ArrayList<>();
        if(pos == LAST_PAGE && ITEMS_REMAINING > 0){
            for(int i=start; i<start + ITEMS_REMAINING; i++){
                movie.add(movies.get(i));
            }
        }else{
            for(int i=start; i<start+numOfData; i++){
                movie.add(movies.get(i));
            }
        }
        return movie;
    }
}