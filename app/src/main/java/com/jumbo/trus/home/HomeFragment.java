package com.jumbo.trus.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jumbo.trus.R;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private TextView tv_oslavenec,tv_random;
    private Button btn_info, btn_obnovit, btn_zavrit;

    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tv_oslavenec = view.findViewById(R.id.tv_oslavenec);
        tv_random = view.findViewById(R.id.tv_random);
        btn_info = view.findViewById(R.id.btn_info);
        btn_obnovit = view.findViewById(R.id.btn_obnovit);
        btn_zavrit = view.findViewById(R.id.btn_zavrit);
        btn_obnovit.setOnClickListener(this);
        homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        homeViewModel.init();
        Log.d(TAG, "onCreateView: ");
        homeViewModel.setRandomFact();
        homeViewModel.getRandomFact().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tv_random.setText(homeViewModel.getRandomFact().getValue());
            }
        });
        homeViewModel.setPlayerBirthday();
        homeViewModel.getPlayerBirthday().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tv_oslavenec.setText(homeViewModel.getPlayerBirthday().getValue());
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_obnovit:
                homeViewModel.setRandomFact();
                homeViewModel.setPlayerBirthday();
                break;
        }
    }
}
