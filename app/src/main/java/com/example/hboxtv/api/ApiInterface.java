package com.example.hboxtv.api;

import com.example.hboxtv.model.CategoryByDeviceModel;
import com.example.hboxtv.model.Category;
import com.example.hboxtv.model.CategoryByDeviceResponse;
import com.example.hboxtv.model.SignInModel;
import com.example.hboxtv.model.SignInResponse;
import com.example.hboxtv.model.SignUpModel;
import com.example.hboxtv.model.SignUpResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    String BASE_URL = "http://54.36.204.161/iptvapi/objects/";

    /*@GET("marvel")
    Call<List<Hero>> getHeroes();*/

    @POST("register.php")
    Call<SignUpResponse> registerUser(@Body SignUpModel body);

    @POST("login.php")
    Call<SignInResponse> loginUser(@Body SignInModel body);

    @POST("categorybydevice.php")
    Call<CategoryByDeviceResponse> getCategories(@Body CategoryByDeviceModel model);
}
