package com.jumbo.trus;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.jumbo.trus.fine.FineFragment;
import com.jumbo.trus.home.HomeFragment;
import com.jumbo.trus.match.MatchFragment;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.notification.NotificationFragment;
import com.jumbo.trus.notification.NotificationViewModel;
import com.jumbo.trus.player.PlayerFragment;
import com.jumbo.trus.playerlist.MatchListFragment;
import com.jumbo.trus.repayment.RepaymentFragment;
import com.jumbo.trus.season.SeasonsFragment;
import com.jumbo.trus.statistics.BeerStatisticsFragment;
import com.jumbo.trus.statistics.FineStatisticsFragment;
import com.jumbo.trus.user.User;
import com.jumbo.trus.user.UserInteractionFragment;

import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "MainActivity";

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private NotificationViewModel notificationViewModel;
    private BadgeDrawable notificationBadge;
    private boolean firstInit = true;

    private Notification lastReadNotification;
    private Notification lastNotification;
    private int notificationsUnread;

    private User user;
    private SharedPreferences pref;


    private NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
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
                    notificationBadge.clearNumber();
                    notificationsUnread = 0;
                    setLastReadNotification(lastNotification);
                    Log.d(TAG, "Přepnuto na navigaci notifikací");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getIntent().getSerializableExtra("user");
        Log.d(TAG, "onCreate: přihlásil se user " + user);
        pref = getSharedPreferences("Notification", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        MainActivityViewModel model = new ViewModelProvider(this).get(MainActivityViewModel.class);
        model.init();
        model.setUser(user);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        navigation = findViewById(R.id.navigation);
        navigation.setOnItemSelectedListener(onItemSelectedListener);
        notificationBadge = navigation.getOrCreateBadge(navigation.getMenu().getItem(4).getItemId());
        notificationBadge.setVisible(true);
        notificationBadge.setMaxCharacterCount(2);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.init();
        notificationViewModel.getNotifications().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                Log.d(TAG, "onChanged: nacetly se notifikace " + notifications);
                lastNotification = notifications.get(0); //uložíme první načtenou modifikaci
                if (firstInit) {
                    if (!findLastLastReadNotificationFromPref()) { //první notifikaci najdeme ze sharedpref
                        notificationBadge.setNumber(10);
                    }
                    else {
                        findNumberOfLastReadNotification();
                    }
                    firstInit = false;
                }
                else {
                    findNumberOfLastReadNotification();
                }
                if (notificationsUnread == 0) {
                    notificationBadge.clearNumber();
                }
                else {
                    Log.d(TAG, "onChanged: tady by se mělo změnit číslo na " + notificationsUnread);
                    notificationBadge.setNumber(notificationsUnread);
                    //notificationBadge.
                }
                Log.d(TAG, "onChanged: notificationsUnread" + notificationsUnread);
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

    private Bundle prepareUserBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        return bundle;
    }

    private void setupViewPager(ViewPager viewPager) {
        BottomNavPagerAdapter adapter = new BottomNavPagerAdapter(getSupportFragmentManager());
        Fragment fragment = new HomeFragment();
        fragment.setArguments(prepareUserBundle());
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
        adapter.addFragment(new UserInteractionFragment());
        adapter.addFragment(new RepaymentFragment());
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

    private void setLastReadNotification(Notification notification) {
        Log.d(TAG, "setLastReadNotification: " + notification);
        lastReadNotification = notification;
        pref.edit().putString("lastReadNotification", notification.getId()).apply();
    }

    private void findNumberOfLastReadNotification() {
        notificationsUnread = 0;
        Log.d(TAG, "findNumberOfLastReadNotification: " + notificationViewModel.getNotifications().getValue());
        for (Notification notification : Objects.requireNonNull(notificationViewModel.getNotifications().getValue())) {
            if (!notification.equals(lastReadNotification)) {
                if (!notification.getUser().equals(user)) {
                    notificationsUnread++;
                }
            }
            else {
                break;
            }
        }
    }

    private boolean findLastLastReadNotificationFromPref() {
        Log.d(TAG, "findLastLastReadNotificationFromPref: seznam" + notificationViewModel.getNotifications().getValue());
        String id = pref.getString("lastReadNotification", "id");
        Log.d(TAG, "findLastLastReadNotificationFromPref: id" + id);
        if (id.equals("id")) {
            setLastReadNotification(lastNotification);
            return true;
        }
        for (Notification notification : Objects.requireNonNull(notificationViewModel.getNotifications().getValue())) {
            if (notification.getId().equals(id)) {
                Log.d(TAG, "findLastLastReadNotificationFromPref: " + notification);
                lastReadNotification = notification;
                return true;
            }
        }
        if (id.equals("id")) { //pro první přihlášení nějakého usera co ještě nemá nastavenou notifikaci v shared preferences
            setLastReadNotification(lastNotification);
        }
        Log.d(TAG, "findLastLastReadNotificationFromPref: return false" );
        return false;
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
            case R.id.user:
                setTitle("Nastavení uživatele");
                viewPager.setCurrentItem(10);
                return true;
            case R.id.owed:
                setTitle("Srovnání dluhů hráčů");
                viewPager.setCurrentItem(11);
                return true;
            default:
                return false;
        }
    }
}