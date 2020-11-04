package com.example.myapplication.codecraftrestaurant.view.adapter;

import android.app.Application;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.codecraftrestaurant.R;
import com.example.myapplication.codecraftrestaurant.databinding.ListRowBinding;
import com.example.myapplication.codecraftrestaurant.databinding.ListRowProgressIndicatorBinding;
import com.example.myapplication.codecraftrestaurant.model.Restaurant__;
import com.example.myapplication.codecraftrestaurant.util.Constants;
import com.example.myapplication.codecraftrestaurant.viewmodel.ProgressIndicatorViewModel;
import com.example.myapplication.codecraftrestaurant.viewmodel.RestaurantListItemViewModel;

import java.util.List;

public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RestaurantListItemViewModel.DataListener {
    private ListRowBinding binding;
    private List<Restaurant__> restaurant__list;
    private Application application;
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    protected boolean isProgressVisible = false;
    private DataListener dataListener;

    public RestaurantRecyclerAdapter(Application application, List<Restaurant__> restaurant__list, DataListener dataListener) {
        this.restaurant__list = restaurant__list;
        this.application = application;
        this.dataListener = dataListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
            return new RestaurantViewHolder(view);
        }
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_progress_indicator, parent, false);
            return new RestaurantsProgressHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RestaurantViewHolder) {
            RestaurantListItemViewModel viewModel = new RestaurantListItemViewModel(application, restaurant__list.get(position), this);
            ((RestaurantViewHolder) holder).getBinding().setViewModel(viewModel);
            ((RestaurantViewHolder) holder).getBinding().executePendingBindings();
            ((RestaurantViewHolder) holder).getBinding().parentLayout.jumpDrawablesToCurrentState();

        }
        if (holder instanceof RestaurantsProgressHolder) {
            ProgressIndicatorViewModel viewModel = new ProgressIndicatorViewModel(application);
            ((RestaurantsProgressHolder) holder).getBinding().setViewModel(viewModel);
            ((RestaurantsProgressHolder) holder).getBinding().executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return restaurant__list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (isProgressVisible) {
            return position == restaurant__list.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
        } else {
            return VIEW_TYPE_DATA;
        }
    }

    public void setRestaurantsList(List<Restaurant__> list) {
        this.restaurant__list = list;
        notifyDataSetChanged();
    }

    @Override
    public ImageView imageview() {
        return binding.image;
    }

    @Override
    public void startActivity(Intent intent) {
        dataListener.startActivity(intent);
    }

    // Provide a reference to the views for each data item
    public class RestaurantViewHolder extends RecyclerView.ViewHolder {


        RestaurantViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        public ListRowBinding getBinding() {
            return binding;
        }

    }

    public void addProgress() {
        isProgressVisible = true;
        restaurant__list.add(new Restaurant__(Constants.INFINITE_SCROLL, null, ""));
        notifyItemInserted(restaurant__list.size() - 1);
    }

    public void removeProgress() {
        isProgressVisible = false;
        if (restaurant__list.get(restaurant__list.size() - 1).getName().equals(Constants.INFINITE_SCROLL)) {
            restaurant__list.remove(restaurant__list.size() - 1);
            notifyItemRemoved(restaurant__list.size() - 1);
        }
    }

    public class RestaurantsProgressHolder extends RecyclerView.ViewHolder {

        private ListRowProgressIndicatorBinding progressIndicatorBinding;

        public RestaurantsProgressHolder(@NonNull View itemView) {
            super(itemView);
            progressIndicatorBinding = DataBindingUtil.bind(itemView);

        }

        public ListRowProgressIndicatorBinding getBinding() {
            return progressIndicatorBinding;
        }
    }

    public interface DataListener {

        void startActivity(Intent intent);
    }
}
