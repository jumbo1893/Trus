package com.jumbo.trus;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.navigation.NavigationView;
import com.jumbo.trus.adapters.recycleview.SimpleRecycleViewAdapter;
import com.jumbo.trus.adapters.recycleview.SimpleStringRecycleViewAdapter;
import com.jumbo.trus.listener.OnListListener;

import java.util.ArrayList;
import java.util.List;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BottomSheetFragment";

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout linearLayout;
    private ImageView img_menu;
    private View view;
    private NavigationView nav_view;

    private INavigationDrawerCallback iNavigationDrawerCallback;


    @Override
    public void onStart() {
        super.onStart();
        sheetBehavior = BottomSheetBehavior.from((View) view.getParent());

        setSheetBehaviorCallback();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.bottom_navigation_drawer, container, false);
        linearLayout = view.findViewById(R.id.bottom_navigation_container);
        img_menu = view.findViewById(R.id.img_menu);
        nav_view = view.findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
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

        return view;
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
