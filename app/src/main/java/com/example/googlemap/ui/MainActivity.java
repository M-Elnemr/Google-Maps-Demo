package com.example.googlemap.ui;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.example.googlemap.R;
import com.example.googlemap.responses.MapsResponse;
import com.example.googlemap.utils.Utils;
import com.example.googlemap.viewModels.MapModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.no_internet)
    TextView noInternet;
    @BindView(R.id.show_hide_btn)
    Button showHideBtn;

    private static final String TAG = "MainActivity";
    private SupportMapFragment mapFragment;
    private GoogleMap googleMaps;
    private MapModel model;
    private boolean permsGranted = false;
    private boolean isMapShown = false;
    private MapsResponse mResponse;

    private List<String> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (checkService()) {
            getLocationPerm();
            if (permsGranted) {
                init();
                dataLoad();
            } else {
                noInternet.setText(R.string.no_service);
                noInternet.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMaps = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);
            }
        }

        try {
            googleMap.setMyLocationEnabled(true);

        } catch (Exception e) {
            Log.d("Error", "onMapReady: " + e.getMessage());
        }

        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        for (int i=0;i<mResponse.getRows().size();i++){
            String title = mResponse.getRows().get(i).getTitle();

//            min Required SDK = 24
//            double[] destination = Stream.of(mResponse.getRows().get(i).getDestinationLongLat().get(0).split(","))
//                    .mapToDouble (Double::parseDouble)
//                    .toArray();

            String data = mResponse.getRows().get(i).getDestinationLongLat().get(0);
            String[] tokens = data.split(",");
            double[] destination = new double[tokens.length];

            int d =0;
            for (String s : tokens){
                destination[d++] = Double.valueOf(s);
            }

            LatLng placeLocation = new LatLng(destination[0], destination[1]);
                Marker placeMarker = googleMap.addMarker(new MarkerOptions().position(placeLocation)
                    .title(title));
                placeMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.metro));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(placeLocation));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1000, null);
        }
    }

    private boolean checkService() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "checkService: amazing");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "checkService: show maps dialog");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, 9001);
            dialog.show();
        } else {
            Log.d(TAG, "checkService: service is not available");
            Toast.makeText(this, "sorry, Maps service is not available for you", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getLocationPerm() {
        String[] prems = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permsGranted = true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, prems, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permsGranted = false;

        switch (requestCode) {
            case 1:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        permsGranted = false;
                        return;
                    }
                }
                permsGranted = true;
        }
    }

    private void init() {
        if (!Utils.isNetworkAvailable(MainActivity.this)) {
            noInternet.setVisibility(View.VISIBLE);
        } else {
            noInternet.setVisibility(View.GONE);
        }
        model = ViewModelProviders.of(this).get(MapModel.class);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        Objects.requireNonNull(mapFragment.getView()).setVisibility(View.GONE);
    }

    private void dataLoad() {

        model.getMaps().observe(this, new Observer<MapsResponse>() {
            @Override
            public void onChanged(MapsResponse mapsResponse) {

                if (mapsResponse != null) {
                    Log.d(TAG, "onChanged: " + mapsResponse.getRows().size());
                    noInternet.setVisibility(View.GONE);
                   mResponse = mapsResponse;

                    assert mapFragment != null;
                    mapFragment.getMapAsync(MainActivity.this);

                } else {
                    Log.d(TAG, "onChanged: null");
                    noInternet.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @OnClick(R.id.show_hide_btn)
    public void onViewClicked() {
        if(isMapShown){
            Objects.requireNonNull(mapFragment.getView()).setVisibility(View.GONE);
            showHideBtn.setText(R.string.show_map);
            isMapShown = false;
        }else {
            Objects.requireNonNull(mapFragment.getView()).setVisibility(View.VISIBLE);
            showHideBtn.setText(R.string.hide_map);
            isMapShown = true;
        }
    }
}
