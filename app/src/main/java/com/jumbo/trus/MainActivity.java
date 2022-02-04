package com.jumbo.trus;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.jumbo.trus.fine.add.FineAddFragment;
import com.jumbo.trus.fine.detail.add.FinePlusFragment;
import com.jumbo.trus.fine.detail.edit.FineEditFragment;
import com.jumbo.trus.fine.detail.list.FineFragment;
import com.jumbo.trus.fine.playerlist.FinePlayersFragment;
import com.jumbo.trus.home.HomeFragment;
import com.jumbo.trus.info.AppInfoFragment;
import com.jumbo.trus.main.NotificationBadgeCounter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.edit.MatchEditFragment;
import com.jumbo.trus.match.list.MatchFragment;
import com.jumbo.trus.match.add.MatchPlusFragment;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.notification.NotificationFragment;
import com.jumbo.trus.notification.NotificationViewModel;
import com.jumbo.trus.pkfl.LoadedMatchesFragment;
import com.jumbo.trus.player.edit.PlayerEditFragment;
import com.jumbo.trus.player.list.PlayerFragment;
import com.jumbo.trus.player.add.PlayerPlusFragment;
import com.jumbo.trus.beer.BeerFragment;

import com.jumbo.trus.repayment.RepaymentFragment;
import com.jumbo.trus.repayment.RepaymentPlusFragment;
import com.jumbo.trus.season.edit.SeasonEditFragment;
import com.jumbo.trus.season.add.SeasonPlusFragment;
import com.jumbo.trus.season.list.SeasonsFragment;

import com.jumbo.trus.statistics.MainStatisticsFragment;
import com.jumbo.trus.statistics.match.beer.detail.BeerMatchStatisticsDetailFragment;
import com.jumbo.trus.statistics.match.fine.detail.FineMatchStatisticsDetailFragment;
import com.jumbo.trus.statistics.player.beer.detail.BeerPlayerStatisticsDetailFragment;
import com.jumbo.trus.statistics.player.fine.detail.FinePlayerStatisticsDetailFragment;
import com.jumbo.trus.statistics.table.TableStatisticsFragment;
import com.jumbo.trus.user.User;
import com.jumbo.trus.user.interaction.AdminInteracionFragment;
import com.jumbo.trus.user.interaction.ApprovePasswordsFragment;
import com.jumbo.trus.user.interaction.ResetPasswordsFragment;
import com.jumbo.trus.user.interaction.UserInteractionFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements INavigationDrawerCallback {

    private static final String TAG = "MainActivity";
    private CustomViewPager viewPager;
    private BottomNavigationView navigation;
    private FloatingActionButton beerButton;
    private NotificationViewModel notificationViewModel;
    private TextView tv_notifications;
    private ImageView img_nav_notification, img_nav_plus;

    private List<Integer> previousFragments = new ArrayList<>();
    //private List<String> pageTitles;

    private SharedPreferences pref;

    private NotificationBadgeCounter notificationBadgeCounter;

    private BottomNavPagerAdapter adapter;

    //pro piva
    private SharedViewModel sharedViewModel;


    private NavigationBarView.OnItemSelectedListener onItemSelectedListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, "onNavigationItemSelected: " + item);
            setToolbarIconsColorToWhite();
            setBottomNavigationButtonsCheckable(true);
            return onMenuItemClicked(item);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        pref = getSharedPreferences("Notification", MODE_PRIVATE);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setUser((User) getIntent().getSerializableExtra("user"));
        sharedViewModel.getMainMatch().observe(this, new Observer<Match>() {
            @Override
            public void onChanged(Match match) {
                if (match != null) {
                    allowNavigationBar(true);
                }
            }
        });
        notificationBadgeCounter = new NotificationBadgeCounter(pref, sharedViewModel.getUser().getValue());
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        navigation = findViewById(R.id.navigation);
        beerButton = findViewById(R.id.beerButton);
        allowNavigationBar(false);
        sharedViewModel.getMainMatch().observe(this, new Observer<Match>() {
            @Override
            public void onChanged(Match match) {
                if (match != null) {
                    allowNavigationBar(true);
                }
            }
        });
        beerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewFragmentBranch(15);
            }
        });
        navigation.setOnItemSelectedListener(onItemSelectedListener);
        navigation.getMenu().getItem(2).setEnabled(false);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.init();
        notificationViewModel.getNotifications().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                setNotificationsBadgeNumber(notificationBadgeCounter.returnNumberOfLastNotification(notifications));
            }

        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    navigation.setSelectedItemId(R.id.nav_home);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        showToolbarBackButton(false);
    }

    private void allowNavigationBar(boolean allow) {
        if (!allow) {
            navigation.setVisibility(View.GONE);
            beerButton.setVisibility(View.GONE);

        }
        else {
            navigation.setVisibility(View.VISIBLE);
            beerButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_navigation, menu);
        final MenuItem notMenuItem = menu.findItem(R.id.nav_notification);
        View notActionView = notMenuItem.getActionView();
        tv_notifications = notActionView.findViewById(R.id.notification_badge);
        img_nav_notification = notActionView.findViewById(R.id.img_nav_notification);
        final MenuItem plusMenuItem = menu.findItem(R.id.nav_plus);
        View plusActionView = plusMenuItem.getActionView();
        img_nav_plus = plusActionView.findViewById(R.id.img_nav_plus);

        notActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(notMenuItem);
            }
        });
        plusActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(plusMenuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setBottomNavigationButtonsCheckable(false);
        setToolbarIconsColorToWhite();
        switch (item.getItemId()) {

            case R.id.nav_notification: {
                startMenuItemNavigation(img_nav_notification, item);
                return true;
            }
            case R.id.nav_plus: {
                startMenuItemNavigation(img_nav_plus, item);
                return true;
            }
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return true;

    }

    private void showBottomSheetNavigation() {
        BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
        bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
    }

    private boolean onMenuItemClicked(MenuItem item) {
        int fragmentNumber;
        switch (item.getItemId()) {
            case R.id.nav_navigation:
                showBottomSheetNavigation();
                return false;
            case R.id.nav_home:
                Log.d(TAG, "Přepnuto na domovskou stránku");
                fragmentNumber = 0;
                showToolbarBackButton(false);
                break;
            case R.id.nav_player_all:
                fragmentNumber = 1;
                break;
            case R.id.nav_settings_season:
                fragmentNumber = 2;
                break;
            case R.id.nav_notification:
                showNotificationFragment(); //3
                return true;
            case R.id.nav_match_all:
                fragmentNumber = 4;
                break;
            case R.id.nav_fine_all:
                fragmentNumber = 7;
                break;
            case R.id.nav_fine:
            case R.id.nav_fine_add:
                fragmentNumber = 6;
                break;
            case R.id.nav_statistics: //+9
                fragmentNumber = 8;
                break;
            case R.id.nav_settings_user:
                fragmentNumber = 10;
                break;
            case R.id.nav_player_repayment:
                fragmentNumber = 11;
                break;
            case R.id.nav_match_pkfl:
                fragmentNumber = 12;
                break;
            case R.id.nav_plus:
            case R.id.nav_match_plus: {
                fragmentNumber = 13;
                break;
            }
            case R.id.nav_beer_add:
                fragmentNumber = 15;
                break;
            case R.id.nav_player_plus:
                fragmentNumber = 17;
                break;
            case R.id.nav_statistics_export:
                fragmentNumber = 28;
                break;
            case R.id.nav_settings_info:
                fragmentNumber = 29;
                break;
            default:
                return false;
        }
        Log.d(TAG, "onMenuItemClicked: " + fragmentNumber);
        setNewPage(fragmentNumber);
        return true;
    }

    public void addNewPreviousFragment() {
        previousFragments.add(viewPager.getCurrentItem());
    }

    public void openPreviousFragment() {
        int size = previousFragments.size();
        Log.d(TAG, "openPreviousFragment: " + previousFragments.size());
        if (size > 0) {
            replaceFragments(previousFragments.get(size - 1));
            previousFragments.remove(size - 1);
        } else if (viewPager.getCurrentItem() != 0) {
            setNewPage(0);
        }
    }

    /**
     * voláme pokud chceme zobrazit fragment, který je zároveň prvním - nelze se z něj vrátit zpět
     */
    private void startNewFragmentBranch(int fragmentId) {
        setNewPage(fragmentId);
        previousFragments.clear();
        decideWhetherToShowToolbarBackButton(); // vždy by mělo bejt bez
    }

    public void replaceFragments(int viewPagerId) {
        setNewPage(viewPagerId);
        decideWhetherToShowToolbarBackButton();
    }

    public void reloadFragment() {
        viewPager.setCurrentItem(viewPager.getCurrentItem());
    }

    public void decideWhetherToShowToolbarBackButton() {
        if (previousFragments.size() == 0) {
            showToolbarBackButton(false);
        } else {
            showToolbarBackButton(true);
        }
    }

    private void showToolbarBackButton(boolean show) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(show);
            getSupportActionBar().setDisplayShowHomeEnabled(show);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new BottomNavPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Trusí appka"); //0
        adapter.addFragment(new PlayerFragment(), "Seznam hráčů"); //1
        adapter.addFragment(new SeasonsFragment(), "Seznam sezon"); //2
        adapter.addFragment(new NotificationFragment(), "Notifikace"); //3
        adapter.addFragment(new MatchFragment(), "Seznam zápasů"); //4
        adapter.addFragment(new MatchFragment(), "Přidat pívo"); //5
        adapter.addFragment(new FinePlayersFragment(), "Přidat pokutu"); //6
        adapter.addFragment(new FineFragment(), "Nastavení pokut"); //7
        adapter.addFragment(new MainStatisticsFragment(), "Statistika"); //8
        adapter.addFragment(new MainStatisticsFragment(), "Statistika pokut"); //9
        adapter.addFragment(new UserInteractionFragment(), "Nastavení"); //10
        adapter.addFragment(new RepaymentFragment(), "Splacení pokut"); //11
        adapter.addFragment(new LoadedMatchesFragment(), "Zápasy PKFL"); //12
        adapter.addFragment(new MatchPlusFragment(), "Přidat zápas"); //13
        adapter.addFragment(new MatchEditFragment(), "Upravit zápas"); //14
        adapter.addFragment(new BeerFragment(), "Přidat piva"); //15
        adapter.addFragment(new FineAddFragment(), "Přidat pokuty hráči"); //16
        adapter.addFragment(new PlayerPlusFragment(), "Přidat hráče"); //17
        adapter.addFragment(new PlayerEditFragment(), "Upravit hráče"); //18
        adapter.addFragment(new SeasonPlusFragment(), "Přidat sezonu"); //19
        adapter.addFragment(new SeasonEditFragment(), "Upravit sezonu"); //20
        adapter.addFragment(new FinePlusFragment(), "Přidat pokutu"); //21
        adapter.addFragment(new FineEditFragment(), "Upravit pokutu"); //22
        adapter.addFragment(new RepaymentPlusFragment(), "Přidat platbu"); //23
        adapter.addFragment(new BeerPlayerStatisticsDetailFragment(), "Stats piv hráče"); //24
        adapter.addFragment(new FinePlayerStatisticsDetailFragment(), "Stats pokut hráče"); //25
        adapter.addFragment(new BeerMatchStatisticsDetailFragment(), "Stats piv v zápase"); //26
        adapter.addFragment(new FineMatchStatisticsDetailFragment(), "Stats pokut v zápase"); //27
        adapter.addFragment(new TableStatisticsFragment(), "Tabulka/export"); //28
        adapter.addFragment(new AppInfoFragment(), "Info o appce"); //29
        adapter.addFragment(new ApprovePasswordsFragment(), "Schválení uživatelů"); //30
        adapter.addFragment(new ResetPasswordsFragment(), "Reset hesel"); //31
        adapter.addFragment(new AdminInteracionFragment(), "Změna uživatelů"); //32

        viewPager.setAdapter(adapter);
    }

    private void setNewPage(int fragmentId) {
        setTitle(adapter.getPagetitle(fragmentId));
        viewPager.setCurrentItem(fragmentId);
    }

    private void startMenuItemNavigation(final ImageView imageView, final MenuItem menuItem) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.getDrawable().setTint(Color.parseColor("#FFB303"));
                onMenuItemClicked(menuItem);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(anim);

    }

    private void showNotificationFragment() {
        startNewFragmentBranch(3);
        setTitle("Notifikace");
        notificationBadgeCounter.setLastReadNotification();
        setNotificationsBadgeNumber(0);
    }

    @SuppressLint("SetTextI18n")
    private void setNotificationsBadgeNumber(int notificationNumber) {
        if (notificationNumber == 0) {
            tv_notifications.setVisibility(View.GONE);
            return;
        } else if (notificationNumber == NotificationBadgeCounter.MAX_NUMBER) {
            tv_notifications.setText(notificationNumber + "+");
        } else {
            tv_notifications.setText(String.valueOf(notificationNumber));
        }
        tv_notifications.setVisibility(View.VISIBLE);
    }

    private void setToolbarIconsColorToWhite() {
        img_nav_notification.getDrawable().setTint(Color.parseColor("#FFFFFF"));
        img_nav_plus.getDrawable().setTint(Color.parseColor("#FFFFFF"));
    }

    private void setBottomNavigationButtonsCheckable(boolean checkable) {
        int size = navigation.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigation.getMenu().getItem(i).setCheckable(checkable);
        }
    }

    @Override
    public boolean onMenuItemPicked(MenuItem item) {
        setBottomNavigationButtonsCheckable(false);
        return onMenuItemClicked(item);
    }
}