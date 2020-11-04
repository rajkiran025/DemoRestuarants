package com.example.myapplication.codecraftrestaurant.service;

import com.example.myapplication.codecraftrestaurant.model.Restaurant__;

import java.util.List;

public interface SessionFacade {

    List<Restaurant__> loadNextListOfRestaurants(int value, double latitude, double longitude);

    List<Restaurant__> fetchAllRestaurants(double latitude, double longitude);

}
