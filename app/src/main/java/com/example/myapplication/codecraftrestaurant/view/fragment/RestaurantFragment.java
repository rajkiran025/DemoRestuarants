package com.example.myapplication.codecraftrestaurant.view.fragment;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.codecraftrestaurant.R;
import com.example.myapplication.codecraftrestaurant.databinding.FragmentRestaurantBinding;
import com.example.myapplication.codecraftrestaurant.model.Restaurant__;
import com.example.myapplication.codecraftrestaurant.service.GpsReceiver;
import com.example.myapplication.codecraftrestaurant.util.Constants;
import com.example.myapplication.codecraftrestaurant.util.Utility;
import com.example.myapplication.codecraftrestaurant.view.activity.MapsMarkerActivity;
import com.example.myapplication.codecraftrestaurant.viewmodel.RestaurantFragmentViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;

public class RestaurantFragment extends BaseFragment implements RestaurantFragmentViewModel.DataListener, SwipeRefreshLayout.OnRefreshListener {

    private FragmentRestaurantBinding binding;
    private RestaurantFragmentViewModel viewModel;
    private OnFragmentChangeInteraction listener;
    private GpsReceiver receiver;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;


    public static RestaurantFragment getInstance() {
        return new RestaurantFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChangeInteraction) {
            listener = (OnFragmentChangeInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant, null, false);
        receiver = new GpsReceiver(new GpsReceiver.LocationCallBack() {
            @Override
            public void onLocationTriggered() {
                //Location state changed
                locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGpsEnabled) {
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            viewModel.getLocation();
                        }
                    });

                }
            }
        });
        context.registerReceiver(receiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        // SwipeRefreshLayout
        ((SwipeRefreshLayout) (binding.swipeContainer)).setOnRefreshListener(this);
        binding.recyclerView.setItemAnimator(null);
        setHasOptionsMenu(true);
        return binding.getRoot();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RestaurantFragmentViewModel.class).build(this);
        viewModel.getRestLiveData().observe(getActivity(), new Observer<List<Restaurant__>>() {
            @Override
            public void onChanged(List<Restaurant__> restaurant__s) {
                viewModel.setNotifyDataChange(restaurant__s);
            }
        });
        binding.setViewModel(viewModel);
    }

    @Override
    public void requestForPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                Constants.PREFERENCE_CODE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //show options menu items only if bottom bar is visible - for home fragment
        inflater.inflate(R.menu.manu_map, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map:
                if (Utility.isLocationEnabled(context)) {
                    startActivity(MapsMarkerActivity.getStartIntent(getActivity()));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showAlertDialog() {
        listener.showAlertDialog();
    }

    @Override
    public void showProgress() {
        listener.showProgress();
    }

    @Override
    public void hideProgress() {
        listener.hideProgress();

    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.recyclerView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constants.PREFERENCE_CODE) {
            mPermissionGrantedCheck(grantResults, permissions);
        }
    }

    public void mPermissionGrantedCheck(int[] grantResults, String[] permissions) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults != null && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                listener.finish();
            } else {
                if (!Utility.isLocationEnabled(context)) {
                    listener.showAlertDialog();
                } else {
                    viewModel.getLocation();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);

    }

    @Override
    public void onRefresh() {
        viewModel.resetValue();
        viewModel.setPulledToRefresh(true);
        ((SwipeRefreshLayout) (binding.swipeContainer)).setRefreshing(false);
    }

    public interface OnFragmentChangeInteraction {

        void showAlertDialog();

        void showProgress();

        void hideProgress();

        void finish();
    }
}


