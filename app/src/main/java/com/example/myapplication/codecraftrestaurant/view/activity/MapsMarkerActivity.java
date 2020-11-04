package com.example.myapplication.codecraftrestaurant.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.codecraftrestaurant.R;
import com.example.myapplication.codecraftrestaurant.databinding.ActivityMapsBinding;
import com.example.myapplication.codecraftrestaurant.model.MapModel;
import com.example.myapplication.codecraftrestaurant.repository.RestaurantRepository;
import com.example.myapplication.codecraftrestaurant.viewmodel.MapsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsMarkerActivity extends FragmentActivity
        implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener{

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MapsMarkerActivity.class);
    }

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMapsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        final MapsViewModel viewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        // Retrieve the content view that renders the map.
        binding.setViewModel(viewModel);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<MapModel> latLongList = RestaurantRepository.getInstance(getApplication()).getLatLongList();
        for (int i = 0; i < latLongList.size(); i++) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLongList.get(i).getLatitude(), latLongList.get(i).getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .title(latLongList.get(i).getName())
                    .snippet(latLongList.get(i).getAddress())
                    .rotation((float) -15.0)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            );

            if (i == 0) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latLongList.get(0).getLatitude(), latLongList.get(0).getLongitude()), 10));
            }

        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }
}