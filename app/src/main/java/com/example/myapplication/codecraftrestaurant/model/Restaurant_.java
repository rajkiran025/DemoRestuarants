
package com.example.myapplication.codecraftrestaurant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Restaurant_ {

    @SerializedName("restaurant")
    @Expose
    private Restaurant__ restaurant;

    public Restaurant__ getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant__ restaurant) {
        this.restaurant = restaurant;
    }

}
