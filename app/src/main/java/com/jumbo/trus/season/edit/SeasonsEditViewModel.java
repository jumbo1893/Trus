package com.jumbo.trus.season.edit;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jumbo.trus.BaseViewModel;
import com.jumbo.trus.Date;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.User;

import java.time.DateTimeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SeasonsEditViewModel extends BaseViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "SeasonsEditViewModel";

    private MutableLiveData<List<Season>> seasons;
    private Season pickedSeason;
    private MutableLiveData<Season> season = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository;
    private boolean changeAlertLocked;

    public void init() {
        changeAlertLocked = true;
        firebaseRepository = new FirebaseRepository(FirebaseRepository.SEASON_TABLE, this);
        if (seasons == null) {
            seasons = new MutableLiveData<>();
            firebaseRepository.loadSeasonsFromRepository();
            Log.d(TAG, "init: nacitam hrace");
        }
    }

    public void editSeasonInRepository(final String name, final String dateStart, final String dateEnd, User user) {
        isUpdating.setValue(true);
        Date date = new Date();
        Season season = pickedSeason;
        season.setName(name);
        try {
            long millisStart = date.convertTextDateToMillis(dateStart);
            long millisEnd = date.convertTextDateToMillis(dateEnd);
            season.setSeasonStart(millisStart);
            season.setSeasonEnd(millisEnd);
            firebaseRepository.editModel(season);
        }
        catch (DateTimeException e) {
            Log.e(TAG, "editPlayerInRepository: toto by nem??lo nastat, o??et??eno validac??", e);
            alert.setValue("Datum mus?? b??t ve form??tu " + Date.DATE_PATTERN);
            return;
        }
        changeAlertLocked = true;
        String text = "Po????te??n?? datum: " + dateStart + ", kone??n?? datum: " + dateEnd;
        sendNotificationToRepository(new Notification("Upravena sezona " + name, text, user));
    }

    public void removeSeasonFromRepository(User user) {
        isUpdating.setValue(true);
        Season season = pickedSeason;
        try {
            firebaseRepository.removeModel(season);
        }
        catch (Exception e) {
            Log.e(TAG, "removePlayerFromRepository: chyba p??i maz??n?? sezony", e);
            alert.setValue("Chyba p??i pos??l??n?? po??adavku do DB");
            return;
        }
        changeAlertLocked = true;
        Date date = new Date();
        String text = "Po????te??n?? datum: " + date.convertMillisSecToTextDate(season.getSeasonStart()) + ", kone??n?? datum: " + date.convertMillisSecToTextDate(season.getSeasonEnd());
        sendNotificationToRepository(new Notification("Smaz??na sezona " + season.getName(), text, user));
    }

    private void sendChangedSeasonAlert() {
        if (!changeAlertLocked) {
            alert.setValue("Pr??v?? n??kdo jin?? upravil sezonu. Reloaduji nov?? ??daje...");
        }
        else {
            changeAlertLocked = false;
        }
    }

    public void setPickedSeason(Season season) {
        pickedSeason = season;
        setSeason(season);
    }

    private void setSeason(Season season) {
        this.season.setValue(season);
    }

    public LiveData<Season> getSeason() {
        return season;
    }

    private void updatePickedSeason(List<Season> seasons) {
        pickedSeason = findSeasonFromRepo(pickedSeason, seasons);
        setSeason(pickedSeason);
        sendChangedSeasonAlert();
    }

    private Season findSeasonFromRepo(Season season, List<Season> seasons) {
        if (seasons != null) {
            for (Season repoSeason : seasons) {
                if (repoSeason.equals(season)) {
                    return repoSeason;
                }
            }
        }
        return season;
    }


    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<List<Season>> getSeasons() {
        return seasons;
    }




    @Override
    public void itemAdded(Model model) {

    }

    @Override
    public void itemChanged(Model model) {
        alert.setValue("Sezona " + model.getName() + " ??sp????n?? upravena");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    @Override
    public void itemDeleted(Model model) {
        alert.setValue("Sezona " + model.getName() + " ??sp????n?? smaz??na");
        isUpdating.setValue(false);
        closeFragment.setValue(true);
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
        updatePickedSeason(models);
    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }

}
