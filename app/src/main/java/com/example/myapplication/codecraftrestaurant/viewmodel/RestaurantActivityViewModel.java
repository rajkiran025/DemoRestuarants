package com.example.myapplication.codecraftrestaurant.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class RestaurantActivityViewModel extends AndroidViewModel {

    public RestaurantActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public RestaurantActivityViewModel build() {
        return this;
    }

}
