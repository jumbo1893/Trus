package com.jumbo.trus.pkfl.stats;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jumbo.trus.R;
import com.jumbo.trus.statistics.TabPagerAdapter;
import com.jumbo.trus.statistics.match.beer.BeerMatchStatisticsFragment;
import com.jumbo.trus.statistics.match.fine.FineMatchStatisticsFragment;
import com.jumbo.trus.statistics.player.beer.BeerPlayerStatisticsFragment;
import com.jumbo.trus.statistics.player.fine.FinePlayerStatisticsFragment;

import java.util.Objects;

public class PkflStatisticsFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "PkflStatisticsFragment";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_pkfl_main_statistics, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.statisticsPkflViewpager);
        setupViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
                Log.d(TAG, "onPageSelected: " + tabLayout.getTabCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new TabPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PkflSeasonStatisticsFragment()); //0
        adapter.addFragment(new PkflAllSeasonStatisticsFragment()); //1
        viewPager.setAdapter(adapter);
    }




    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabSelected: " + tab.getPosition());
        viewPager.setCurrentItem(tab.getPosition());
        Log.d(TAG, "onTabSelected: " + viewPager.getCurrentItem());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
