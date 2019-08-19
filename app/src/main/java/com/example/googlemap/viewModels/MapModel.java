package com.example.googlemap.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.googlemap.repositories.MapRepository;
import com.example.googlemap.responses.MapsResponse;

public class MapModel extends AndroidViewModel {

    private LiveData<MapsResponse> maps ;
    private MapRepository mapRepository;

    public MapModel(@NonNull Application application) {
        super(application);
        mapRepository = new MapRepository();
        maps = mapRepository.getMaps();
    }

    public LiveData<MapsResponse> getMaps(){
        return maps;
    }

}
