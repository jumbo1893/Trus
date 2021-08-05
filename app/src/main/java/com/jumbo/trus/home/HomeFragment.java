package com.jumbo.trus.home;

import android.content.Intent;
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
import com.jumbo.trus.user.LoginActivity;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private TextView tv_oslavenec,tv_random;
    private Button btn_info, btn_obnovit, btn_logout;

    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tv_oslavenec = view.findViewById(R.id.tv_oslavenec);
        tv_random = view.findViewById(R.id.tv_random);
        btn_info = view.findViewById(R.id.btn_info);
        btn_obnovit = view.findViewById(R.id.btn_obnovit);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_obnovit.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
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

    private void logoutToLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("logout", true);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_obnovit:
                homeViewModel.setRandomFact();
                homeViewModel.setPlayerBirthday();
                break;
            case R.id.btn_logout:
                logoutToLoginActivity();
                break;
        }
    }
}
