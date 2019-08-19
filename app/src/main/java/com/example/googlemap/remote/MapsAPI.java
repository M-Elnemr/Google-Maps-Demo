package com.example.googlemap.remote;

import com.example.googlemap.responses.MapsResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MapsAPI {

    @GET("metro.json")
    Call<MapsResponse> getDestinations();
}
