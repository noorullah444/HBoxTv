package com.example.hboxtv.api;

import com.example.hboxtv.model.SignInModel;
import com.example.hboxtv.model.SignUpModel;
import com.example.hboxtv.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    String BASE_URL = "http://54.36.204.161/iptvapi/objects/";

    /*@GET("marvel")
    Call<List<Hero>> getHeroes();*/

    @POST("register.php")
    Call<ApiResponse> registerUser(@Body SignUpModel body);

    @POST("login.php")
    Call<ApiResponse> loginUser(@Body SignInModel body);

    /*@GET("movie/{id}")
    Call<Hero> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);*/
}
