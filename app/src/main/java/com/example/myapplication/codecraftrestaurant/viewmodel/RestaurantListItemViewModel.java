package com.example.myapplication.codecraftrestaurant.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;

import com.example.myapplication.codecraftrestaurant.model.Restaurant__;
import com.example.myapplication.codecraftrestaurant.repository.RestaurantRepository;
import com.example.myapplication.codecraftrestaurant.util.Constants;
import com.example.myapplication.codecraftrestaurant.view.activity.RestaurantDetailsActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestaurantListItemViewModel extends AndroidViewModel {

    private ObservableField<String> headerText = new ObservableField<>("");
    private ObservableField<String> subText = new ObservableField<>("");
    private ObservableField<Bitmap> showImage = new ObservableField<Bitmap>();
    private DataListener dataListener;
    private String featuredImage;

    public RestaurantListItemViewModel(@NonNull Application application, Restaurant__ restaurant__, DataListener dataListener) {
        super(application);
        this.dataListener = dataListener;
        setData(restaurant__);

    }

    public ObservableField<Bitmap> getShowImage() {
        return showImage;
    }

    @BindingAdapter({"android:srcImage"})
    public static void showImage(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    private void setData(final Restaurant__ restaurant__) {
        RestaurantRepository.getInstance(getApplication()).setRestLat(Double.parseDouble(restaurant__.getLocation().getLatitude()));
        RestaurantRepository.getInstance(getApplication()).setRestLong(Double.parseDouble(restaurant__.getLocation().getLongitude()));
        headerText.set(restaurant__.getName());
        if (restaurant__.getLocation() != null) {
            subText.set(restaurant__.getLocation().getAddress());
        } else {
            subText.set("");
        }

        featuredImage = restaurant__.getFeaturedImage();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                showImage.set(getBitmapFromURL(restaurant__.getThumb()));
            }
        });
        thread.start();
    }

    public static Bitmap getBitmapFromURL(String src) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(src);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public ObservableField<String> getHeaderText() {
        return headerText;
    }

    public ObservableField<String> getSubText() {
        return subText;
    }

    public interface DataListener {

        ImageView imageview();

        void startActivity(Intent intent);
    }

    public View.OnClickListener onItemClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestaurantDetailsActivity.getStartIntent(v.getContext());
                intent.putExtra(Constants.FEATURED_IMAGE, featuredImage);
                dataListener.startActivity(intent);

            }
        };
    }

}
