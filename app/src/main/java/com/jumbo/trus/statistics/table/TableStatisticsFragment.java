package com.jumbo.trus.statistics.table;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jumbo.trus.CustomViewPager;
import com.jumbo.trus.R;
import com.jumbo.trus.statistics.TabPagerAdapter;
import com.jumbo.trus.statistics.table.beer.TableBeerStatisticsFragment;
import com.jumbo.trus.statistics.table.detail.TableFineDetailStatisticsFragment;
import com.jumbo.trus.statistics.table.fine.TableFineMatchStatisticsFragment;

import java.util.Objects;

public class TableStatisticsFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static final String TAG = "TableStatisticsFragment";

    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private TabPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_table_statistics, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.tableViewpager);
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
        adapter.addFragment(new TableBeerStatisticsFragment()); //0
        adapter.addFragment(new TableFineMatchStatisticsFragment()); //1
        adapter.addFragment(new TableFineDetailStatisticsFragment()); //2
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
