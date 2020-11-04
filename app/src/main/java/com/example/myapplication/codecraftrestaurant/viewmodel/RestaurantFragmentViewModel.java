package com.example.myapplication.codecraftrestaurant.viewmodel;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.codecraftrestaurant.model.MapModel;
import com.example.myapplication.codecraftrestaurant.model.Restaurant__;
import com.example.myapplication.codecraftrestaurant.repository.RestaurantRepository;
import com.example.myapplication.codecraftrestaurant.service.GpsTracker;
import com.example.myapplication.codecraftrestaurant.service.SessionFacadeImpl;
import com.example.myapplication.codecraftrestaurant.util.Constants;
import com.example.myapplication.codecraftrestaurant.view.adapter.RestaurantRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantFragmentViewModel extends AndroidViewModel implements RestaurantRecyclerAdapter.DataListener {
    private GpsTracker gpsTracker;
    private DataListener dataListener;
    private Context mContext;
    protected ObservableField<LinearLayoutManager> layoutManager = new ObservableField<>();
    private ObservableField<RestaurantRecyclerAdapter> recyclerViewAdapter = new ObservableField<>();
    private RestaurantRecyclerAdapter adapter;
    private MutableLiveData<List<Restaurant__>> restLiveData;
    private List<Restaurant__> restaurantList;
    private double latitude;
    private double longitude;
    private int resultsStart = 0;
    private boolean userScrolled = false;
    protected boolean isLoading;
    private int prevResultsSize;
    private List<Restaurant__> updtedRestaurantList;


    public RestaurantFragmentViewModel(@NonNull Application application) {
        super(application);
        mContext = application.getApplicationContext();
        restLiveData = new MutableLiveData<>();
    }

    public void init(List<Restaurant__> restaurantList) {
        restLiveData.setValue(restaurantList);
    }

    public MutableLiveData<List<Restaurant__>> getRestLiveData() {
        return restLiveData;
    }

    public ObservableField<LinearLayoutManager> getLayoutManager() {
        return layoutManager;
    }

    public ObservableField<RestaurantRecyclerAdapter> getRecyclerViewAdapter() {
        return recyclerViewAdapter;
    }

    public RestaurantFragmentViewModel build(DataListener dataListener) {
        this.dataListener = dataListener;
        requestFroPermission();
        return this;
    }

    private void requestFroPermission() {
        dataListener.requestForPermission();
        if (checkLocationPermission()) {
            // getLocation();
        }
    }

    public boolean checkLocationPermission() {
        return (ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    // checks for the location permission
    public void getLocation() {
        gpsTracker = new GpsTracker(getApplication());
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            RestaurantRepository.getInstance(getApplication()).setCurrentLat(latitude);
            RestaurantRepository.getInstance(getApplication()).setCurrentLong(latitude);
            Log.d("Location", latitude + " " + longitude);
            showData();
        } else {
            dataListener.showAlertDialog();
        }
    }

    public void showData() {
        new FetchAllRestaurants().execute();
        dataListener.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = layoutManager.get().findLastVisibleItemPosition();
                int totalItemCount = layoutManager.get().getItemCount();
                int count = Integer.parseInt(RestaurantRepository.getInstance(getApplication()).getResFound());
                if (userScrolled && (totalItemCount < count) && !isLoading
                        && lastVisibleItem + 1 == totalItemCount) {
                    adapter.addProgress();
                    int startcount = Integer.parseInt(RestaurantRepository.getInstance(getApplication()).getResultsStart());
                    int itemshown = Integer.parseInt(RestaurantRepository.getInstance(getApplication()).getResultsShown());
                    if (itemshown != 0) {
                        resultsStart = startcount + itemshown;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new FetchNextListOfRestaurants().execute();
                                userScrolled = false;
                            }
                        }, 2000);
                        isLoading = true;
                    }

                }
                // reset the page to 1 for the next search results
                if (totalItemCount == count) {
                    resultsStart = 0;
                }
            }
        });

    }

    public void setPulledToRefresh(boolean isRefreshed) {
        RestaurantRepository.getInstance(getApplication()).setPulledToRefresh(isRefreshed);
    }

    @Override
    public void startActivity(Intent intent) {
        dataListener.startActivity(intent);
    }

    public void resetValue() {
        resultsStart = 0;
        if (restaurantList != null) {
            restaurantList.clear();
        }
        getLocation();
    }

    public class FetchAllRestaurants extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            isLoading = true;
            dataListener.showProgress();
            // do something  // show some progress of loading images
        }

        @Override
        protected Void doInBackground(String... str) {
            restaurantList = SessionFacadeImpl.getInstance(getApplication()).fetchAllRestaurants(latitude, longitude);
            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            // do something
            isLoading = false;
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    loadFirstSetOfRestaurants(restaurantList);
                    init(restaurantList);
                    dataListener.hideProgress();
                }
            };
            handler.post(runnable);

        }


    }

    public class FetchNextListOfRestaurants extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            isLoading = true;
            // do something  // show some progress of loading images
        }

        @Override
        protected Void doInBackground(String... str) {
            updtedRestaurantList = SessionFacadeImpl.getInstance(getApplication()).loadNextListOfRestaurants(resultsStart, latitude, longitude);
            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            // do something
            isLoading = false;

            loadNextSetOfData(updtedRestaurantList);
        }


    }

    protected void loadNextSetOfData(final List<Restaurant__> results) {
        adapter.removeProgress();
        dataListener.hideProgress();
        if (!results.isEmpty()) {
            restaurantList.addAll(results);
            getLatLongList(restaurantList);
            adapter.notifyItemRangeInserted(prevResultsSize - 1, results.size());
            prevResultsSize = restaurantList.size();
        }
    }

    private List<MapModel> getLatLongList(List<Restaurant__> restaurantList) {
        List<MapModel> latLongList = new ArrayList<>();
        for (int i = 0; i < restaurantList.size(); i++) {
            MapModel mapModel = new MapModel();
            mapModel.setLatitude(Double.parseDouble(restaurantList.get(i).getLocation().getLatitude()));
            mapModel.setLongitude(Double.parseDouble(restaurantList.get(i).getLocation().getLongitude()));
            mapModel.setName(restaurantList.get(i).getName());
            mapModel.setAddress(restaurantList.get(i).getLocation().getAddress());
            latLongList.add(mapModel);
            RestaurantRepository.getInstance(getApplication()).setLatLongList(latLongList);
        }
        return latLongList;
    }

    public void loadFirstSetOfRestaurants(List<Restaurant__> result) {
        prevResultsSize = restaurantList.size();
        restaurantList = new ArrayList<>();
        restaurantList.addAll(result);
        getLatLongList(restaurantList);
        layoutManager.set(new LinearLayoutManager(getApplication().getApplicationContext()));
        adapter = new RestaurantRecyclerAdapter(getApplication(), unique(restaurantList), this);
        recyclerViewAdapter.set(adapter);
        adapter.notifyDataSetChanged();
        dataListener.getRecyclerView().setItemViewCacheSize(Constants.CACHED_SIZ);
        dataListener.hideProgress();

    }

    private List<Restaurant__> unique(List<Restaurant__> list) {
        List<Restaurant__> uniqueList = new ArrayList<>();
        Set<Restaurant__> uniqueSet = new HashSet<>();
        for (Restaurant__ obj : list) {
            if (uniqueSet.add(obj)) {
                uniqueList.add(obj);
            }
        }
        return uniqueList;
    }

    public void setNotifyDataChange(List<Restaurant__> restaurantList) {
        adapter.setRestaurantsList(restaurantList);
    }

    public interface DataListener {
        void requestForPermission();

        void showAlertDialog();

        void showProgress();

        void hideProgress();

        RecyclerView getRecyclerView();

        void startActivity(Intent intent);
    }

}
