package com.example.myapplication.codecraftrestaurant.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.codecraftrestaurant.R;
import com.example.myapplication.codecraftrestaurant.databinding.ActivityRestaurantBinding;
import com.example.myapplication.codecraftrestaurant.service.GpsReceiver;
import com.example.myapplication.codecraftrestaurant.view.fragment.RestaurantFragment;
import com.example.myapplication.codecraftrestaurant.viewmodel.RestaurantActivityViewModel;

public class RestaurantActivity extends AppCompatActivity implements RestaurantFragment.OnFragmentChangeInteraction {
    private Toolbar toolbar;
    private GpsReceiver receiver;
    private ProgressDialog progressDialog;
    private boolean isFromSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRestaurantBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);
        final RestaurantActivityViewModel viewModel = new ViewModelProvider(this).get(RestaurantActivityViewModel.class).build();
        toolbar = binding.getRoot().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.restaurants));
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.setViewmodel(viewModel);
        addFragment(RestaurantFragment.getInstance());
    }

    public void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.parent_content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RestaurantActivity.this);

        alertDialog.setTitle(getResources().getString(R.string.dialog_title));

        alertDialog
                .setMessage(getResources().getString(R.string.gps_msg));

        alertDialog.setPositiveButton(getResources().getString(R.string.dilog_posticve_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        isFromSetting = true;
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(getResources().getString(R.string.dilog_negative_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.setCancelable(false);

        alertDialog.show();
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(RestaurantActivity.this, R.style.MyProgressTheme);
        }
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);

        progressDialog.show();

    }


    @Override
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}