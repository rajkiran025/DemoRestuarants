package com.example.myapplication.codecraftrestaurant.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.codecraftrestaurant.R;
import com.example.myapplication.codecraftrestaurant.databinding.ActivityRestaurantsDetailsBinding;
import com.example.myapplication.codecraftrestaurant.util.Constants;
import com.example.myapplication.codecraftrestaurant.view.fragment.DetailsFragment;
import com.example.myapplication.codecraftrestaurant.viewmodel.DetailsActivityViewModel;

public class RestaurantDetailsActivity extends AppCompatActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RestaurantDetailsActivity.class);
    }

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRestaurantsDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurants_details);
        final DetailsActivityViewModel viewModel = new ViewModelProvider(this).get(DetailsActivityViewModel.class).build();
        toolbar = binding.getRoot().findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.setViewmodel(viewModel);
        String featuredImage = getIntent().getStringExtra(Constants.FEATURED_IMAGE);

        addFragment(DetailsFragment.getInstance(featuredImage));
    }

    public void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.parent_content, fragment);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //replaces the default 'Back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }
}
