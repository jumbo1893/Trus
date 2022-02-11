package com.jumbo.trus.fine.add;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.jumbo.trus.Flag;
import com.jumbo.trus.INotificationSender;
import com.jumbo.trus.Model;
import com.jumbo.trus.comparator.OrderByNonplayerFine;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.listener.ModelLoadedListener;
import com.jumbo.trus.match.Compensation;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.repository.FirebaseRepository;
import com.jumbo.trus.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FineAddViewModel extends ViewModel implements ChangeListener, INotificationSender {

    private static final String TAG = "FineAddViewModel";

    private List<Fine> fineList = new ArrayList<>();
    private Match pickedMatch;
    private Player pickedPlayer;
    private List<Player> pickedPlayers;
    private MutableLiveData<List<ReceivedFine>> fines = new MutableLiveData<>();
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<Boolean> closeFragment = new MutableLiveData<>();
    private MutableLiveData<String> alert = new MutableLiveData<>();
    private MutableLiveData<String> titleText = new MutableLiveData<>();
    private boolean multiplayers;
    private Compensation matchCompensation;
    private boolean changeAlertEnabled = false;
    private FirebaseRepository firebaseRepository;

    public void init(Match match, Player player, List<Player> playerList, boolean multiplayers) {
        if (firebaseRepository == null) {
            firebaseRepository = new FirebaseRepository(FirebaseRepository.MATCH_TABLE, this);
        }
        setPickedMatchAndPlayer(match, player, playerList, multiplayers);
        changeAlertEnabled = false;
        firebaseRepository.loadMatchesFromRepository();
        firebaseRepository.loadFinesFromRepository();
    }

    private void setPickedMatchAndPlayer(Match match, Player player, List<Player> playerList, boolean multiplayers) {
        Log.d(TAG, "setPickedMatchAndPlayer: " + match + ", " + player + ", " + multiplayers);
        pickedMatch = match;
        pickedPlayer = player;
        pickedPlayers = playerList;
        this.multiplayers = multiplayers;
        setFines(fineList);
        initCompensationVariables();
        setTitleText(pickedMatch, player);
    }

    private boolean updatePlayersFines(List<Integer> finesPlus, User user) {
        StringBuilder resultText = new StringBuilder();
        for (Player player : pickedPlayers) {
            for (int i = 0; i < finesPlus.size(); i++) {
                int count = finesPlus.get(i);
                if (count > 0) {
                    if (!player.addNewFineCount(fines.getValue().get(i).getFine(), count)) {
                        resultText.append("Nelze přidat pokutu ").append(fines.getValue().get(i).getName()).append(" hráči ").append(player.getName()).append(". Pravděpodobně ji nemá v repertoáru\n");
                        Log.e(TAG, "onClick: Chyba při přidávání pokut " + fines.getValue().get(i).getName() + " hráči " + player.getName());
                    }
                }
            }
        }
        pickedMatch.mergePlayerLists(pickedPlayers);
        try {
            firebaseRepository.editModel(pickedMatch);
        } catch (Exception e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Něco se posralo při přidávání do db, zkus to znova");
            isUpdating.setValue(false);
            closeFragment.setValue(true);
            return false;
        }
        resultText.append("Měním pokuty u zápasu se soupeřem ").append(pickedMatch.getOpponent()).append(" u hráčů: ");
        for (Player player : pickedPlayers) {
            resultText.append(player.getName()).append(", ");
        }
        Notification notification = new Notification().prepareNotificationAboutChangedFinesInPlayerList(pickedMatch, fines.getValue(), finesPlus, pickedPlayers);
        sendNotification(notification, user);
        alert.setValue(resultText.toString());
        return true;
    }

    public Match editMatchPlayersFines(List<Integer> finesPlus, User user) {
        isUpdating.setValue(true);
        changeAlertEnabled = true;
        if (multiplayers) {
            updatePlayersFines(finesPlus, user);

        } else {
            updatePlayerFines(finesPlus, user);
        }
        return pickedMatch;
    }


    private boolean updatePlayerFines(List<Integer> finesPlus, User user) {
        List<ReceivedFine> fineList = mergeReceivedFinesAndNumber(finesPlus);
        if (!pickedMatch.changePlayerFinesInPlayerList(pickedPlayer, fineList)) {
            alert.setValue("Nelze nalézt hráče v zápase, nesmazal ho nějakej zmrd?");
            isUpdating.setValue(false);
            closeFragment.setValue(true);
            return false;
        }
        try {
            firebaseRepository.editModel(pickedMatch);
        } catch (Exception e) {
            Log.e(TAG, "editMatchInRepository: toto by nemělo nastat, ošetřeno validací", e);
            alert.setValue("Něco se posralo při přidávání do db, zkus to znova");
            isUpdating.setValue(false);
            closeFragment.setValue(true);
            return false;
        }
        Notification notification = new Notification(pickedMatch, pickedPlayer, fineList);
        sendNotification(notification, user);
        alert.setValue("Měním pokuty u zápasu se soupeřem " + pickedMatch.getOpponent() + " u hráče " + pickedPlayer.getName());
        return true;
    }

    private List<ReceivedFine> mergeReceivedFinesAndNumber(List<Integer> finesPlus) {
        List<ReceivedFine> fines = new ArrayList();
        matchCompensation.getFinesCompesation().get(pickedMatch.returnPlayerListWithoutFans().indexOf(pickedPlayer)).clear();
        for (int i = 0; i < finesPlus.size(); i++) {
            if (finesPlus.get(i) > 0) {
                fines.add(new ReceivedFine(this.fines.getValue().get(i).getFine(), finesPlus.get(i)));
            }
        }
        return fines;
    }

    private void setMatchAsAdded(final Match match) {
        Log.d(TAG, "setMatchAsAdded: " + match.getName());
        isUpdating.setValue(false);
        closeFragment.setValue(true);
    }

    private void initCompensationVariables() {
        matchCompensation = new Compensation(pickedMatch);
        matchCompensation.initBeerAndLiquorCompensation();
        matchCompensation.initFineCompensation();
    }

    private void mergeFineListFromLoadedFines(List<Fine> fines) {
        Log.d(TAG, "mergeFineListFromLoadedFines: ");
        pickedPlayer.mergeFineLists(fines);
        Log.d(TAG, "mergeFineListFromLoadedFines: ");
        if (pickedPlayer.isMatchParticipant()) {
            Collections.sort(pickedPlayer.getReceivedFines(), new OrderByNonplayerFine(true));
        } else {
            Collections.sort(pickedPlayer.getReceivedFines(), new OrderByNonplayerFine(false));
        }
        this.fines.setValue(pickedPlayer.getReceivedFines());
    }

    private void setTitleText(Match match, Player player) {
        if (match != null) {
            String text = match.toStringNameWithOpponent() + " | ";
            if (!multiplayers) {
                text += player.getName();
            } else {
                text += "více hráčů";
            }
            titleText.setValue(text);
            return;
        }
        titleText.setValue("...načítám");
    }

    private void sendNotification(Notification notification, User user) {
        notification.setUser(user);
        sendNotificationToRepository(notification);
    }

    @Override
    public void sendNotificationToRepository(Notification notification) {
        firebaseRepository.addNotification(notification);
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<Boolean> closeFragment() {
        return closeFragment;
    }

    public LiveData<String> getAlert() {
        return alert;
    }

    public LiveData<String> getTitleText() {
        return titleText;
    }

    public LiveData<List<ReceivedFine>> getFines() {
        return fines;
    }

    private void getFinesFromReceivedFines(List<Fine> fines) {
        List<ReceivedFine> fineList = new ArrayList();
        for (Fine fine : fines) {
            fineList.add(new ReceivedFine(fine, 0));
        }
        Collections.sort(fineList, new OrderByNonplayerFine(true));
        this.fines.setValue(fineList);
    }

    private void setFines(List<Fine> fines) {
        if (multiplayers) {
            getFinesFromReceivedFines(fines);
        } else {
            mergeFineListFromLoadedFines(fines);
        }
    }

    private void updateMatchFromLoadedMatches(List<Match> matches) {
        Log.d(TAG, "updateMatchFromLoadedMatches: inituji");
        for (Match match : matches) {
            if (match.equals(pickedMatch)) {
                if (changeAlertEnabled) {
                    Log.d(TAG, "updateMatchFromLoadedMatches: nastala změna");
                    alert.setValue("Proběhla změna zápasu, reloaduji nové údaje");
                } else {
                    changeAlertEnabled = true;
                }
                pickedMatch = match;
                initCompensationVariables();
                setTitleText(match, pickedPlayer);
                setNewPlayer(match);
                setFines(fineList);
                break;
            }

        }
    }

    public void removeReg() {
        firebaseRepository.removeListener();
    }

    private void setNewPlayer(Match match) {
        Player newPlayer = match.returnPlayerFromMatch(pickedPlayer);
        if (newPlayer != null) {
            pickedPlayer = newPlayer;
        }

    }

    @Override
    public void itemAdded(Model model) {
    }

    @Override
    public void itemChanged(Model model) {
        setMatchAsAdded((Match) model);
    }

    @Override
    public void itemDeleted(Model model) {
    }

    @Override
    public void itemListLoaded(final List models, Flag flag) {
        Log.d(TAG, "itemListLoaded: " + models);
        List list = new ArrayList(models);
        if (flag.equals(Flag.FINE)) {
            setFines(list);
            fineList = list;
        }
        else if (flag.equals(Flag.MATCH)) {
            Match match = (Match) list.get(0);
            Log.d(TAG, "itemListLoaded: " + match.returnPlayerListOnlyWithParticipants().get(0).getNumberOfBeers());
            Collections.sort(list, new Comparator<Match>() {
                @Override
                public int compare(Match o1, Match o2) {
                    return Long.compare(o2.getDateOfMatch(), o1.getDateOfMatch());
                }
            });

            updateMatchFromLoadedMatches(list);
        }

    }

    @Override
    public void alertSent(String message) {
        alert.setValue(message);
    }
}
