package com.example.weatherforecast.ui.map;


import static com.example.weatherforecast.common.Constants.HANGZHOU;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.example.weatherforecast.R;
import com.example.weatherforecast.controller.viewModel.UserViewModel;
import com.example.weatherforecast.controller.factory.UserViewModelFactory;

public class MapFragment extends FragmentBase {
    protected static CameraPosition cameraPosition;
    protected LatLng latLng;
    private UserViewModel userViewModel;

    @Override
    LatLng getTarget() {
        if(latLng!=null) return latLng;
        Log.d("MapFragment","latLng is null return HANGZHOU");
        return HANGZHOU;
    }

    @Override
    CameraPosition getCameraPosition() {
        return cameraPosition;
    }

    @Override
    void setCameraPosition(CameraPosition cameraPosition) {
        MapFragment.cameraPosition = cameraPosition;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // 获取ViewModel 实例
        userViewModel = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(requireActivity())
        ).get(UserViewModel.class);
        Log.d("MapFragment", "userViewModel: " + (userViewModel == null ? "null" : "initialized"));

        userViewModel.getUserCityLatLng().observe(requireActivity(), new Observer<LatLng>() {
            @Override
            public void onChanged(LatLng latLng1) {
                if(latLng1!=null){
                    latLng=latLng1;
                    Log.d("MapFragment","latLng get success:"+latLng);
                }else{
                    Log.d("MapFragment","latLng get failed:latLng is null");
                }
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // 强制刷新 userViewModel 数据
        userViewModel.refreshUserData();
    }
}
