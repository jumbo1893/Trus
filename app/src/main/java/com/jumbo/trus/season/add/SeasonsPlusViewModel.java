package com.jumbo.trus.season.add;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.Result;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.User;

import java.time.DateTimeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SeasonsPlusViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "SeasonsPlusViewModel";


    private FirebaseRepository firebaseRepository;
    private MutableLiveData<List<Season>> seasons;


    public void init() {

        firebaseRepository = new FirebaseRepository(FirebaseRepository.SEASON_TABLE, this);
        if (seasons == null) {
            seasons = new MutableLiveData<>();
            firebaseRepository.loadSeasonsFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
    }

    public void addSeasonToRepository(final String name, final String dateStart, final String dateEnd, User user) {
        isUpdating.setValue(true);
        Date date = new Date();
        try {
            long millisStart = date.convertTextDateToMillis(dateStart);
            long millisEnd = date.convertTextDateToMillis(dateEnd);
            Season season = new Season(name, millisStart, millisEnd);
            firebaseRepository.insertNewModel(season);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "addSeasonToRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Datum musí být ve formátu " + Date.DATE_PATTERN);
            isUpdating.setValue(false);
            return;
        }
        String text = "Počáteční datum: " + dateStart + ", konečné datum: " + dateEnd;
        sendNotificationToRepository(new Notification("Přidána sezona " + name, text, user));
    }

    private void setSeasonAsAdded (final Season season) {
        Log.d(TAG, "setSeasonAsAdded: " + season.getName());
        alert.setValue("Sezona " + season.getName() + " úspěšně přidána");
        isUpdating.setValue(false);
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }



    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }


    @Override
    public void itemAdded(Model model) {
        setSeasonAsAdded((Season) model);
        closeFragment.setValue(true);
    }

    @Override
    public void itemChanged(Model model) {

    }

    @Override
    public void itemDeleted(Model model) {

    }

    @Override
    public void itemListLoaded(List models, Flag flag) {
        Log.d(TAG, "itemListLoaded: " + models);
        Collections.sort(models, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return Long.compare(o2.getSeasonStart(), o1.getSeasonStart());
            }
        });
        seasons.setValue(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
