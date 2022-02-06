package com.jumbo.trus.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.listener.NotificationListener;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.season.Season;
import com.jumbo.trus.user.User;

import java.util.ArrayList;

public class FirebaseRepository {

    private static final String TAG = "FirebaseRepository";
    public static final String SEASON_TABLE = "season";
    public static final String PLAYER_TABLE = "player_test";
    public static final String FINE_TABLE = "fine";
    public static final String MATCH_TABLE = "match_test";
    public static final String USER_TABLE = "user_test";
    public static final String NOTIFICATION_TABLE = "notification_test";
    public static final String PKFL_TABLE = "pkfl";

    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ChangeListener changeListener;
    private ItemLoadedListener itemLoadedListener;
    private NotificationListener notificationListener;
    private ArrayList<Model> modelsDataSet = new ArrayList<>();

    private boolean allSeasons = false;
    private boolean otherSeason = false;

    public FirebaseRepository(ChangeListener changeListener) {
        this.changeListener = changeListener;
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseRepository(ChangeListener changeListener, ItemLoadedListener itemLoadedListener) {
        this.changeListener = changeListener;
        this.itemLoadedListener = itemLoadedListener;
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseRepository(String keyword, ChangeListener changeListener) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(keyword);
        this.changeListener = changeListener;
    }

    public FirebaseRepository(String keyword, ChangeListener changeListener, boolean allSeasons, boolean otherSeason) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(keyword);
        this.changeListener = changeListener;
        this.allSeasons = allSeasons;
        this.otherSeason = otherSeason;
    }

    public FirebaseRepository(String keyword, NotificationListener notificationListener) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(keyword);
        this.notificationListener = notificationListener;
    }

    public FirebaseRepository(ItemLoadedListener itemLoadedListener) {
        db = FirebaseFirestore.getInstance();
        this.itemLoadedListener = itemLoadedListener;
    }

    public void insertNewModel(final Model model) {
        DocumentReference newPlayerRef = collectionReference.document();

        newPlayerRef.set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: " + task.isSuccessful());
                if (task.isSuccessful()) {
                    changeListener.itemAdded(model);
                }
                else {
                    changeListener.alertSent("Chyba při přidání " + model.getClass().getSimpleName() + " " + model.getName() + " do db");
                }
            }
        });
    }

    public void setItemLoadedListener(ItemLoadedListener itemLoadedListener) {
        this.itemLoadedListener = itemLoadedListener;
    }

    public void editModel(final Model model) {
        DocumentReference newPlayerRef = collectionReference.document(model.getId());

        newPlayerRef.set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: " + task.isSuccessful());
                if (task.isSuccessful()) {
                    changeListener.itemChanged(model);
                }
                else {
                    changeListener.alertSent("Chyba při přidání " + model.getClass().getSimpleName() + " " + model.getName() + " do db");
                }
            }
        });
    }

    public void removeModel(final Model model) {
        collectionReference.document(model.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,  model.getId() + " úspěšně smazán z db ");
                        changeListener.itemDeleted(model);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error při přidávání " + model.getId() + " do databáze ", e);
                        changeListener.alertSent("Chyba při smazání " + model.getClass().getSimpleName() + " " + model.getName() + " z db");
                    }
                });

    }

    public void loadSeasonsFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference collectionReference1 = db.collection(SEASON_TABLE);
        collectionReference1.orderBy("seasonStart", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba při načítání sezon z db", error);
                    return;
                }
                modelsDataSet.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Season season = documentSnapshot.toObject(Season.class);
                    season.setId(documentSnapshot.getId());
                    modelsDataSet.add(season);

                }
                if (allSeasons) {
                    Season all =  new Season().allSeason();
                    modelsDataSet.add(0, all);
                }
                if (otherSeason) {
                    Season other = new Season().otherSeason();
                    modelsDataSet.add(other);
                }
                Log.d(TAG, "Automaticky načten seznam sezon z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.SEASON);
            }
        });
    }

    public void loadFinesFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference collectionReference1 = db.collection(FINE_TABLE);
        collectionReference1.orderBy("name", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba při načítání pokut z db", error);
                    return;
                }
                modelsDataSet.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Fine fine = documentSnapshot.toObject(Fine.class);
                    fine.setId(documentSnapshot.getId());
                    modelsDataSet.add(fine);

                }

                Log.d(TAG, "Automaticky načten seznam pokut z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.FINE);
            }
        });
    }

    public void loadPlayersFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference collectionReference = db.collection(PLAYER_TABLE);
        collectionReference.orderBy("name", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba při načítání hráčů z db", error);
                    return;
                }
                modelsDataSet.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Player player = documentSnapshot.toObject(Player.class);
                    player.setId(documentSnapshot.getId());
                    modelsDataSet.add(player);

                }
                Log.d(TAG, "Automaticky načten seznam hráčů z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.PLAYER);
            }
        });
    }

    public void loadMatchesFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference collectionReference = db.collection(MATCH_TABLE);
        collectionReference.orderBy("dateOfMatch", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetMatchesEvent: nastala chyba při načítání zápasů z db", error);
                    return;
                }
                modelsDataSet.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Match match = documentSnapshot.toObject(Match.class);
                    match.setId(documentSnapshot.getId());
                    modelsDataSet.add(match);

                }
                Log.d(TAG, "Automaticky načten seznam zápasů z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.MATCH);

            }
        });
    }

    public void loadUsersFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference collectionReference = db.collection(USER_TABLE);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetMatchesEvent: nastala chyba při načítání uživatelů z db", error);
                    return;
                }
                modelsDataSet.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    User user = documentSnapshot.toObject(User.class);
                    user.setId(documentSnapshot.getId());
                    modelsDataSet.add(user);

                }
                Log.d(TAG, "Automaticky načten seznam uživatelů z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.USER);

            }
        });
    }

    public void loadNotificationsFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference crNotification = db.collection(NOTIFICATION_TABLE);
        crNotification.orderBy("timestamp", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba při načítání notifikací z db", error);
                    return;
                }
                modelsDataSet.clear();
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    Notification notification = documentSnapshot.toObject(Notification.class);
                    notification.setId(documentSnapshot.getId());
                    modelsDataSet.add(notification);

                }
                Log.d(TAG, "Automaticky načten seznam notifikaci z db ");
                notificationListener.notificationListLoaded(modelsDataSet);

            }
        });
    }

    public void loadPkflUrlFromRepository() {
        CollectionReference collectionReference = db.collection(PKFL_TABLE);
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetMatchesEvent: nastala chyba při načítání pkfl věcí z db", error);
                    return;
                }
                for(QueryDocumentSnapshot documentSnapshot : value) {
                    if (documentSnapshot.getId().equals("url")) {
                        String url = (String) documentSnapshot.get("URL");
                        itemLoadedListener.itemLoaded(url);
                        break;
                    }
                }
            }
        });
    }

    public void addNotification(final Notification notification) {
        Log.d(TAG, "addNotification: " + notification);
        CollectionReference crNotification = db.collection(NOTIFICATION_TABLE);
        DocumentReference newPlayerRef = crNotification.document();

        newPlayerRef.set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: " + task.isSuccessful());
                if (task.isSuccessful()) {
                }
                else {
                    changeListener.alertSent("Chyba při přidání notifikace " + notification + " do db");
                }
            }
        });
    }
}
