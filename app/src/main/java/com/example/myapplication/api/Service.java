package com.example.myapplication.api;

import com.example.myapplication.model.ResponseMovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")
    Call<ResponseMovie> getPopular(
            @Query("api_key") String api_key
    );
}