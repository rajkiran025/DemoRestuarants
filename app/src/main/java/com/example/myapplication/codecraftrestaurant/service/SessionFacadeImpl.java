package com.example.myapplication.codecraftrestaurant.service;

import android.app.Application;

import com.example.myapplication.codecraftrestaurant.model.Restaurant__;
import com.example.myapplication.codecraftrestaurant.repository.RestaurantRepository;

import java.util.List;

public class SessionFacadeImpl implements SessionFacade {
    private RestaurantRepository restaurantRepository;
    private static SessionFacade sessionFacade;

    private SessionFacadeImpl(Application application) {
        restaurantRepository = RestaurantRepository.getInstance(application);
    }

    public static synchronized SessionFacade getInstance(Application application) {
        if (sessionFacade == null) {
            sessionFacade = new SessionFacadeImpl(application);
        }
        return sessionFacade;
    }

    public List<Restaurant__> loadNextListOfRestaurants(int value, double latitude, double longitude) {
        return restaurantRepository.loadNextListOfRestaurants(value, latitude, longitude);
    }

    public List<Restaurant__> fetchAllRestaurants(double latitude, double longitude) {
        return restaurantRepository.fetchAllRestaurants(latitude, longitude);
    }
}
