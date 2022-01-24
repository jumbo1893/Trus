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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.jumbo.trus.fine.add.FineAddFragment;
import com.jumbo.trus.fine.FineFragment;
import com.jumbo.trus.fine.add.FineAddViewModel;
import com.jumbo.trus.fine.playerlist.FinePlayersFragment;
import com.jumbo.trus.fine.playerlist.FinePlayersViewModel;
import com.jumbo.trus.home.HomeFragment;
import com.jumbo.trus.main.NotificationBadgeCounter;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.match.edit.MatchEditFragment;
import com.jumbo.trus.match.matchlist.MatchFragment;
import com.jumbo.trus.match.add.MatchPlusFragment;
import com.jumbo.trus.match.MatchAllViewModel;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.notification.NotificationFragment;
import com.jumbo.trus.notification.NotificationViewModel;
import com.jumbo.trus.pkfl.LoadedMatchesFragment;
import com.jumbo.trus.player.PlayerEditFragment;
import com.jumbo.trus.player.PlayerFragment;
import com.jumbo.trus.player.PlayerPlusFragment;
import com.jumbo.trus.playerlist.beer.BeerFragment;

import com.jumbo.trus.playerlist.beer.BeerViewModel;
import com.jumbo.trus.repayment.RepaymentFragment;
import com.jumbo.trus.season.SeasonEditFragment;
import com.jumbo.trus.season.SeasonPlusFragment;
import com.jumbo.trus.season.SeasonsFragment;
import com.jumbo.trus.statistics.BeerStatisticsFragment;
import com.jumbo.trus.statistics.FineStatisticsFragment;

import com.jumbo.trus.user.LoginViewModel;
import com.jumbo.trus.user.User;
import com.jumbo.trus.user.UserInteractionFragment;

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
    private List<String> pageTitles;

    private User user;
    private SharedPreferences pref;
    private Match pickedMatch;

    private NotificationBadgeCounter notificationBadgeCounter;

    private BottomNavPagerAdapter adapter;

    //pro piva
    private MatchAllViewModel matchAllViewModel;
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
        user = (User) getIntent().getSerializableExtra("user");
        Log.d(TAG, "onCreate: přihlásil se user " + user);
        setTheme(R.style.AppTheme);
        pref = getSharedPreferences("Notification", MODE_PRIVATE);
        notificationBadgeCounter = new NotificationBadgeCounter(pref, user);
        setContentView(R.layout.activity_main);
        LoginViewModel model = new ViewModelProvider(this).get(LoginViewModel.class);
        model.init();
        model.setUser(user);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        setupPageTitles();
        navigation = findViewById(R.id.navigation);
        beerButton = findViewById(R.id.beerButton);
        beerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedViewModel.getMainMatch().getValue() != null) {
                    /*adapter.getItem(15).setArguments(prepareMatchBundle());
                    viewPager.setAdapter(adapter);*/
                    setBottomNavigationButtonsCheckable(false);
                    startNewFragmentBranch(15);
                } else {
                    Toast.makeText(MainActivity.this, "Vydrž než se načtou zápasy", Toast.LENGTH_SHORT).show();
                }
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
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        matchAllViewModel = new ViewModelProvider(this).get(MatchAllViewModel.class);
        matchAllViewModel.init();
        matchAllViewModel.getMatches().observe(this, new Observer<List<Match>>() {
            @Override
            public void onChanged(List<Match> matches) {
                Log.d(TAG, "onChanged: nacetli se hraci " + matches);
                if (sharedViewModel.getMainMatch().getValue() == null) {
                    sharedViewModel.findLastMatch(matches);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
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
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        showToolbarBackButton(false);
    }

    private void initViewModels() {
        FinePlayersViewModel finePlayersViewModel;
        FineAddViewModel fineAddViewModel;
        BeerViewModel beerViewModel;
        fineAddViewModel = new ViewModelProvider(this).get(FineAddViewModel.class);
        beerViewModel = new ViewModelProvider(this).get(BeerViewModel.class);
        finePlayersViewModel = new ViewModelProvider(this).get(FinePlayersViewModel.class);
        beerViewModel.init();
        fineAddViewModel.init();
        finePlayersViewModel.init();
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
            default:
                return false;
        }
        Log.d(TAG, "onMenuItemClicked: " + fragmentNumber);
        setNewPage(fragmentNumber);
        return true;
    }

    private Bundle prepareUserBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        return bundle;
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
        Fragment fragment = new HomeFragment();
        fragment.setArguments(prepareUserBundle());
        adapter.addFragment(new HomeFragment()); //0
        adapter.addFragment(new PlayerFragment()); //1
        adapter.addFragment(new SeasonsFragment()); //2
        adapter.addFragment(new NotificationFragment()); //3
        adapter.addFragment(new MatchFragment()); //4
        adapter.addFragment(new MatchFragment()); //5
        adapter.addFragment(new FinePlayersFragment()); //6
        adapter.addFragment(new FineFragment()); //7
        adapter.addFragment(new BeerStatisticsFragment()); //8
        adapter.addFragment(new FineStatisticsFragment()); //9
        adapter.addFragment(new UserInteractionFragment()); //10
        adapter.addFragment(new RepaymentFragment()); //11
        adapter.addFragment(new LoadedMatchesFragment()); //12
        adapter.addFragment(new MatchPlusFragment()); //13
        adapter.addFragment(new MatchEditFragment()); //14
        adapter.addFragment(new BeerFragment()); //15
        adapter.addFragment(new FineAddFragment()); //16
        adapter.addFragment(new PlayerPlusFragment()); //17
        adapter.addFragment(new PlayerEditFragment()); //18
        adapter.addFragment(new SeasonPlusFragment()); //19
        adapter.addFragment(new SeasonEditFragment()); //20
        viewPager.setAdapter(adapter);
    }

    private void setupPageTitles() {
        pageTitles = new ArrayList<>();
        pageTitles.add("Trusí appka"); //0
        pageTitles.add("Seznam zápasů"); //1
        pageTitles.add("Seznam sezon"); //2
        pageTitles.add("Notifikace"); //3
        pageTitles.add("Seznam zápasů"); //4
        pageTitles.add("Přidat pívo"); //5
        pageTitles.add("Přidat pokutu"); //6
        pageTitles.add("Nastavení pokut"); //7
        pageTitles.add("Statistika Piv"); //8
        pageTitles.add("Statistika pokut"); //9
        pageTitles.add("Nastavení"); //10
        pageTitles.add("Splacení pokut"); //11
        pageTitles.add("Zápasy PKFL"); //12
        pageTitles.add("Přidat zápas"); //13
        pageTitles.add("Upravit zápas"); //14
        pageTitles.add("Přidat piva"); //15
        pageTitles.add("Přidat pokuty hráči"); //16
        pageTitles.add("Přidat hráče"); //17
        pageTitles.add("Upravit hráče"); //18
        pageTitles.add("Přidat sezonu"); //19
        pageTitles.add("Upravit sezonu"); //20
    }

    private void setNewPage(int fragmentId) {
        Log.d(TAG, "setNewPage: " + pageTitles.get(fragmentId).getClass().getSimpleName());
        setTitle(pageTitles.get(fragmentId));
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

    public Match findPickedMatchFromList(Match pickedMatch, List<Match> matches) {
        if (pickedMatch != null && matches != null) {
            for (Match match : matches) {
                if (match.equalsByOpponentName(pickedMatch)) {
                    return match;
                }
            }
        }
        return findLastMatch(matches);
    }

    public Match findLastMatch(List<Match> matches) {
        if (matches != null && matches.size() > 0) {
            return matches.get(0);
        }
        Log.d(TAG, "findLastMatch: Nelze najít žádný zápas!!");
        return null;
    }


    @Override
    public boolean onMenuItemPicked(MenuItem item) {
        setBottomNavigationButtonsCheckable(false);
        return onMenuItemClicked(item);
    }
}