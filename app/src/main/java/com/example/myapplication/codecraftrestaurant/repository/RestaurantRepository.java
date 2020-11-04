package com.example.myapplication.codecraftrestaurant.repository;

import android.app.Application;

import com.example.myapplication.codecraftrestaurant.model.Location;
import com.example.myapplication.codecraftrestaurant.model.MapModel;
import com.example.myapplication.codecraftrestaurant.model.Restaurant__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantRepository extends BaseRepository {

    private static RestaurantRepository bootstrapRepository;
    private String resultsStart;
    private String resultsShown;
    private boolean isRefreshed;
    private String resFound;
    private double currentLat;
    private double currentLong;
    private double restLat;
    private double restLong;
    private List<MapModel> latLongList;
    private List<Restaurant__> restnameList;
    //private List<>

    private RestaurantRepository(Application application) {
        super(application);
    }

    public static RestaurantRepository getInstance(Application application) {
        if (bootstrapRepository == null) {
            bootstrapRepository = new RestaurantRepository(application);
        }
        return bootstrapRepository;
    }

    public String getResFound() {
        return resFound;
    }

    public void setResFound(String resFound) {
        this.resFound = resFound;
    }

    public String getResultsStart() {
        return resultsStart;
    }

    public String getResultsShown() {
        return resultsShown;
    }

    public void setResultsStart(String resultsStart) {
        this.resultsStart = resultsStart;
    }

    public void setResultsShown(String resultsShown) {
        this.resultsShown = resultsShown;
    }

    public String getRestaurants(String uri) {
        StringBuilder sb = new StringBuilder();
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(uri);

            urlConnection = (HttpURLConnection) url
                    .openConnection();
            Map<String, String> headers = new HashMap<>();

            headers.put("user-key", "31d45e069f5906a63a99c174a1b0eae0");
            headers.put("content-type", "application/json");

            for (String headerKey : headers.keySet()) {
                urlConnection.setRequestProperty(headerKey, headers.get(headerKey));
            }
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                sb.append(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //regardless of success or failure we will disconnect from the HttpUrlConnection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return sb.toString();

    }

    public List<Restaurant__> fetchAllRestaurants(double latitude, double longitude) {
        String uri = "https://developers.zomato.com/api/v2.1/search?lat=" + latitude + "&lon=" + longitude;
        String request = getRestaurants(uri);
        return getlist(request);
    }

    public List<Restaurant__> loadNextListOfRestaurants(int value, double latitude, double longitude) {
        String request = null;
        String uri = "https://developers.zomato.com/api/v2.1/search?start=" + value + "&lat=" + latitude + "&lon=" + longitude;
        request = getRestaurants(uri);
        return getlist(request);

    }


    public List<Restaurant__> getlist(String request) {
        List<Restaurant__> restaurantsList = new ArrayList<>();
        JSONObject root = null;
        try {
            root = new JSONObject(request);
            String resourceFound = root.getString("results_found");
            String resStart = root.getString("results_start");
            String resShown = root.getString("results_shown");
            setResultsStart(resStart);
            setResultsShown(resShown);
            setResFound(resourceFound);

            JSONArray restaurants = root.getJSONArray("restaurants");
            for (int i = 0; i < restaurants.length(); i++) {
                JSONObject res = restaurants.getJSONObject(i).getJSONObject("restaurant");

                String name = res.getString("name");
                JSONObject locationObj = res.getJSONObject("location");
                String address = locationObj.getString("address");
                String latitude = locationObj.getString("latitude");
                String longitude = locationObj.getString("longitude");
                String imageUrl = res.getString("thumb");
                String featuredImage = res.getString("featured_image");

                Restaurant__ resObj = new Restaurant__();
                resObj.setName(name);
                Location location = new Location();
                location.setAddress(address);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                resObj.setLocation(location);
                resObj.getLocation().setAddress(location.getAddress());
                resObj.setThumb(imageUrl);
                resObj.setFeaturedImage(featuredImage);
                restaurantsList.add(resObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return restaurantsList;

    }

    public boolean isRefreshed() {
        return isRefreshed;
    }

    public void setPulledToRefresh(boolean isRefreshed) {
        this.isRefreshed = isRefreshed;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setRestLat(double restLat) {
        this.restLat = restLat;
    }

    public double getRestLat() {
        return restLat;
    }

    public void setRestLong(double restLong) {
        this.restLong = restLong;
    }

    public double getRestLong() {
        return restLong;
    }

    public void setLatLongList(List<MapModel> latLongList) {
        this.latLongList = latLongList;
    }

    public List<MapModel> getLatLongList() {
        return latLongList;
    }

    public void setRestnameList(List<Restaurant__> restnameList) {
        this.restnameList = restnameList;
    }

    public List<Restaurant__> getRestnameList() {
        return restnameList;
    }
}
