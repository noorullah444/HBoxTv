package com.example.hboxtv.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hboxtv.R;
import com.example.hboxtv.api.ApiInterface;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        getHeroes();
    }

    /*private void getHeroes() {
        //Creating a call instance using our RetrofitClient
        //here basically we are calling the method getHeroes() that we created inside
        //our API Interface
        apiInterface = ApiClient.getInstance().getMyApi();
        Call<List<Hero>> call = apiInterface.getHeroes();

        //to perform the API call we need to call the method enqueue()
        //We need to pass a Callback with enqueue method
        //And Inside the callback functions we will handle success or failure of
        //the result that we got after the API call
        call.enqueue(new Callback<List<Hero>>() {
            @Override
            public void onResponse(Call<List<Hero>> call, Response<List<Hero>> response) {

                //In this point we got our hero list
                //thats damn easy right ;)
                if (response.body() != null) {
                    List<Hero> heroList = response.body();
                    Log.d(TAG, "onResponse: response= " + heroList.toString());
                }

                //now we can do whatever we want with this list


            }

            @Override
            public void onFailure(Call<List<Hero>> call, Throwable t) {
                //handle error or failure cases here
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}