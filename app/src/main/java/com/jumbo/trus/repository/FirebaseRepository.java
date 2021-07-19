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
import com.jumbo.trus.ChangeListener;
import com.jumbo.trus.Flag;
import com.jumbo.trus.NotificationListener;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.Model;
import com.jumbo.trus.notification.Notification;
import com.jumbo.trus.season.Season;

import java.util.ArrayList;

public class FirebaseRepository {

    private static final String TAG = "FirebaseRepository";

    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private ChangeListener changeListener;
    private NotificationListener notificationListener;
    private ArrayList<Model> modelsDataSet = new ArrayList<>();

    public FirebaseRepository(ChangeListener changeListener) {
        this.changeListener = changeListener;
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseRepository(String keyword, ChangeListener changeListener) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(keyword);
        this.changeListener = changeListener;
    }

    public FirebaseRepository(String keyword, NotificationListener notificationListener) {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(keyword);
        this.notificationListener = notificationListener;
    }

    public void setCollectionDB(String keyword) {
        collectionReference = db.collection(keyword);
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
        CollectionReference collectionReference1 = db.collection("season");
        collectionReference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                Log.d(TAG, "Automaticky načten seznam sezon z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.SEASON);
            }
        });
    }

    public void loadFinesFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference collectionReference1 = db.collection("fine");
        collectionReference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        CollectionReference collectionReference = db.collection("player");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        CollectionReference collectionReference = db.collection("match");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void loadNotificationsFromRepository() {
        modelsDataSet = new ArrayList<>();
        CollectionReference crNotification = db.collection("notification");
        //crNotification.orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        crNotification.orderBy("timestamp", Query.Direction.DESCENDING).limit(10).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba při načítání hráčů z db", error);
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

    public void addNotification(final Notification notification) {
        Log.d(TAG, "addNotification: " + notification);
        CollectionReference crNotification = db.collection("notification");
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
