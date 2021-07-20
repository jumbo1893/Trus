package com.jumbo.trus;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.notification.NotificationViewModel;
import com.jumbo.trus.playerlist.MatchListFragment;
import com.jumbo.trus.fine.FineFragment;
import com.jumbo.trus.fine.FineViewModel;
import com.jumbo.trus.home.HomeFragment;
import com.jumbo.trus.match.MatchFragment;
import com.jumbo.trus.match.MatchViewModel;
import com.jumbo.trus.notification.NotificationFragment;
import com.jumbo.trus.player.PlayerFragment;
import com.jumbo.trus.player.PlayerViewModel;
import com.jumbo.trus.season.SeasonsFragment;
import com.jumbo.trus.season.SeasonsViewModel;
import com.jumbo.trus.statistics.BeerStatisticsFragment;
import com.jumbo.trus.statistics.FineStatisticsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private NotificationViewModel notificationViewModel;
    private BottomNavigationItemView itemView;
    private boolean firstInit = true;

    private Notification lastReadNotification;
    private Notification lastNotification;

    public User user = new User("test");

    private static final String TAG = "MainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    viewPager.setCurrentItem(0);
                    setTitle("Trusí appka");
                    Log.d(TAG, "Přepnuto na domovskou stránku");
                    return true;
                case R.id.nav_plus:
                    showPlusPopup(navigation);
                    Log.d(TAG, "Přepnuto na navigaci přidání");
                    return true;
                case R.id.nav_settings:
                    showSettingsPopup(navigation);
                    Log.d(TAG, "Přepnuto na navigaci nastavení");
                    return true;
                case R.id.nav_statistics:
                    showStatisticsPopup(navigation);
                    Log.d(TAG, "Přepnuto na navigaci statistiky");
                    return true;
                case R.id.nav_notification:
                    viewPager.setCurrentItem(3);
                    setTitle("Notifikace");
                    removeBadge();
                    lastReadNotification = lastNotification;
                    //badge.setVisibility(View.GONE);
                    Log.d(TAG, "Přepnuto na navigaci notifikací");
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        initNotificationBadge();
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.init();
        notificationViewModel.getNotifications().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                Log.d(TAG, "onChanged: nacetly se notifikace " + notifications);
                lastNotification = notifications.get(0); //uložíme první načtenou modifikaci
                if (firstInit) {
                    Log.d(TAG, "firstinit: ");
                    lastReadNotification = lastNotification; //první modifikaci označíme při startu jako poslední přečtenou - od této počítáme
                    firstInit = false;
                }
                int unread = 0;
                for (Notification notification : notifications) {
                    if (!notification.equals(lastReadNotification)) {
                        unread++;
                    }
                    else {
                        break;
                    }
                }
                if (unread == 1) {
                    showBadge(unread); //to znamená, že se badge předtím odebíral nebo ještě nebyl načten
                }
                else if (unread > 1) { //musíme předtím odebrat aby nebyl 2x
                    showBadge(unread);
                    removeBadge();
                }
            }

        });

        // Hide the activity toolbar
        //getSupportActionBar().hide();
        //Initializing viewPager

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigation.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        //navigation.setSelectedItemId(R.id.nav_plus);
                        break;
                    case 2:
                        //navigation.setSelectedItemId(R.id.nav_settings);
                        break;
                    case 3:
                        //navigation.setSelectedItemId(R.id.nav_notification);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void setupViewPager(ViewPager viewPager) {
        BottomNavPagerAdapter adapter = new BottomNavPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new PlayerFragment());
        adapter.addFragment(new SeasonsFragment());
        adapter.addFragment(new NotificationFragment());
        adapter.addFragment(new MatchFragment());
        adapter.addFragment(new MatchListFragment(Flag.BEER));
        adapter.addFragment(new MatchListFragment(Flag.FINE));
        adapter.addFragment(new FineFragment());
        adapter.addFragment(new BeerStatisticsFragment());
        adapter.addFragment(new FineStatisticsFragment());
        viewPager.setAdapter(adapter);
    }

    public void showPlusPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.plus_popup, popup.getMenu());
        popup.show();
    }

    public void showSettingsPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.settings_popup, popup.getMenu());
        popup.show();
    }

    public void showStatisticsPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.statistics_popup, popup.getMenu());
        popup.show();
    }

    private void initNotificationBadge() {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(4);
        itemView = (BottomNavigationItemView) v;
    }
    private void showBadge(int numberUnread) {
        View badge = LayoutInflater.from(this).inflate(R.layout.notification_badge, itemView, true);
        TextView textView = badge.findViewById(R.id.notificationsbbadge);
        if (numberUnread > 8) {
            textView.setText("9+");
        }
        else {
            textView.setText(" " + numberUnread + " ");
        }
    }

    public void removeBadge() {
        itemView.removeViewAt(itemView.getChildCount()-1);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.player:
                viewPager.setCurrentItem(1);
                setTitle("Přidat hráče");
                //item.collapseActionView();
                return true;
            case R.id.match:
                setTitle("Přidat zápas");
                viewPager.setCurrentItem(4);
                return true;
            case R.id.beer:
                setTitle("Přidat piva");
                viewPager.setCurrentItem(5);
                return true;
            case R.id.season:
                setTitle("Upravit sezony");
                viewPager.setCurrentItem(2);
                return true;
            case R.id.fine:
                setTitle("Upravit pokuty");
                viewPager.setCurrentItem(7);
                return true;
            case R.id.penalty:
                setTitle("Přidat pokuty");
                viewPager.setCurrentItem(6);
                return true;
            case R.id.statistics_beer:
                setTitle("Statistika piv");
                viewPager.setCurrentItem(8);
                return true;
            case R.id.statistics_fine:
                setTitle("Statistika pokut");
                viewPager.setCurrentItem(9);
                return true;
            default:
                return false;
        }
    }
}