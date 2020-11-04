package com.example.myapplication.codecraftrestaurant.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class DetailsActivityViewModel extends AndroidViewModel {

    public DetailsActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public DetailsActivityViewModel build() {
        return this;
    }
}
