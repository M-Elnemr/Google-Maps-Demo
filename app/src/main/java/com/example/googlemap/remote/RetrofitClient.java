package com.example.googlemap.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static MapsAPI mapsAPI = null ;
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "http://atpnet.net/";

    public static MapsAPI getAPI(){
        if(mapsAPI == null){
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mapsAPI = retrofit.create(MapsAPI.class);
        }
        return mapsAPI;
    }

}
