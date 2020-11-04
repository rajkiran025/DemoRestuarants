package com.example.myapplication.codecraftrestaurant.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.codecraftrestaurant.R;
import com.example.myapplication.codecraftrestaurant.databinding.FragmentDetailsBinding;
import com.example.myapplication.codecraftrestaurant.util.Constants;
import com.example.myapplication.codecraftrestaurant.viewmodel.DetailsFragmentViewModel;

public class DetailsFragment extends BaseFragment {

    private FragmentDetailsBinding binding;

    public static DetailsFragment getInstance(String featuredImage) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FEATURED_IMAGE, featuredImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, null, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final DetailsFragmentViewModel viewModel = new ViewModelProvider(this).get(DetailsFragmentViewModel.class).build(getArguments());
        binding.setViewModel(viewModel);
        binding.image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.bringToFront();
                viewModel.viewTransformation(v, event);
                return true;
            }
        });
    }
}
