package com.jumbo.trus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.jumbo.trus.user.login.LoginActivity;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BottomSheetFragment";

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout linearLayout;
    private ImageView img_menu;
    private View view;
    private NavigationView nav_view;
    private Button btnLogout;
    private TextView tvUserName;
    private SharedViewModel sharedViewModel;


    private INavigationDrawerCallback iNavigationDrawerCallback;


    @Override
    public void onStart() {
        super.onStart();
        sheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        setSheetBehaviorCallback();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.bottom_navigation_drawer, container, false);
        linearLayout = view.findViewById(R.id.bottom_navigation_container);
        img_menu = view.findViewById(R.id.img_menu);
        nav_view = view.findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        View header = nav_view.getHeaderView(0);
        btnLogout = header.findViewById(R.id.btnLogout);
        tvUserName = header.findViewById(R.id.tvUserName);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        tvUserName.setText("píč " + sharedViewModel.getUser().getValue().getName());
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutToLoginActivity();
            }
        });

        return view;
    }

    private void logoutToLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("logout", true);
        startActivity(intent);
    }

    private void setSheetBehaviorCallback() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                img_menu.setRotation(slideOffset * 180);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof INavigationDrawerCallback) {
            iNavigationDrawerCallback = (INavigationDrawerCallback) context;
        }
        else {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected: " + item);
        if (iNavigationDrawerCallback.onMenuItemPicked(item)) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        return false;
    }
}
