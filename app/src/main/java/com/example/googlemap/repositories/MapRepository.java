package com.example.googlemap.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.googlemap.remote.MapsAPI;
import com.example.googlemap.remote.RetrofitClient;
import com.example.googlemap.responses.MapsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapRepository {

    private MapsAPI mapsAPI;
    private MutableLiveData<MapsResponse> maps = new MutableLiveData<>();

    public MapRepository(){
        mapsAPI = RetrofitClient.getAPI();
    }

    public LiveData<MapsResponse> getMaps(){

        mapsAPI.getDestinations().enqueue(new Callback<MapsResponse>() {
            @Override
            public void onResponse(Call<MapsResponse> call, Response<MapsResponse> response) {
                if(response.isSuccessful()){
                    if (response.body() != null){
                        maps.postValue(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<MapsResponse> call, Throwable t) {

            }
        });
        return maps;
    }


}
