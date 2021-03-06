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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jumbo.trus.Flag;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.listener.ChangeListener;
import com.jumbo.trus.listener.ItemLoadedListener;
import com.jumbo.trus.listener.ModelLoadedListener;
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
    private ModelLoadedListener modelLoadedListener;
    private NotificationListener notificationListener;
    private ArrayList<Model> modelsDataSet = new ArrayList<>();
    private ListenerRegistration matchesListener;
    private ListenerRegistration matchListener;

    private boolean allSeasons = false;
    private boolean otherSeason = false;


    public FirebaseRepository(ChangeListener changeListener) {
        this.changeListener = changeListener;

    }

    public FirebaseRepository(ChangeListener changeListener, ItemLoadedListener itemLoadedListener) {
        this.changeListener = changeListener;
        this.itemLoadedListener = itemLoadedListener;
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
    }

    public FirebaseRepository(String keyword, ChangeListener changeListener) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        collectionReference = db.collection(keyword);
        this.changeListener = changeListener;
    }

    public FirebaseRepository(String keyword, ChangeListener changeListener, ModelLoadedListener modelLoadedListener) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        collectionReference = db.collection(keyword);
        this.changeListener = changeListener;
        this.modelLoadedListener = modelLoadedListener;
    }

    public FirebaseRepository(String keyword, ChangeListener changeListener, boolean allSeasons, boolean otherSeason) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        collectionReference = db.collection(keyword);
        this.changeListener = changeListener;
        this.allSeasons = allSeasons;
        this.otherSeason = otherSeason;
    }

    public FirebaseRepository(String keyword, ChangeListener changeListener, boolean allSeasons, boolean otherSeason, ModelLoadedListener modelLoadedListener) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        collectionReference = db.collection(keyword);
        this.changeListener = changeListener;
        this.modelLoadedListener = modelLoadedListener;
        this.allSeasons = allSeasons;
        this.otherSeason = otherSeason;
    }

    public FirebaseRepository(String keyword, NotificationListener notificationListener) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        collectionReference = db.collection(keyword);
        this.notificationListener = notificationListener;
    }

    public FirebaseRepository(ItemLoadedListener itemLoadedListener) {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        this.itemLoadedListener = itemLoadedListener;
    }

    public void removeListener() {
        if (matchesListener != null) {
            matchesListener.remove();
        }
        if (matchListener != null) {
            matchListener.remove();
        }
    }

    public void insertNewModel(final Model model) {
        final DocumentReference newPlayerRef = collectionReference.document();

        newPlayerRef.set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: " + task.isSuccessful());
                if (task.isSuccessful()) {
                    model.setId(newPlayerRef.getId());
                    Log.d(TAG, "onComplete: " + newPlayerRef.getId());
                    changeListener.itemAdded(model);
                } else {
                    changeListener.alertSent("Chyba p??i p??id??n?? " + model.getClass().getSimpleName() + " " + model.getName() + " do db");
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
                } else {
                    changeListener.alertSent("Chyba p??i p??id??n?? " + model.getClass().getSimpleName() + " " + model.getName() + " do db");
                }
            }
        });
    }

    public void removeModel(final Model model) {
        collectionReference.document(model.getId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, model.getId() + " ??sp????n?? smaz??n z db ");
                        changeListener.itemDeleted(model);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error p??i p??id??v??n?? " + model.getId() + " do datab??ze ", e);
                        changeListener.alertSent("Chyba p??i smaz??n?? " + model.getClass().getSimpleName() + " " + model.getName() + " z db");
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
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba p??i na????t??n?? sezon z db", error);
                    return;
                }
                modelsDataSet.clear();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Season season = documentSnapshot.toObject(Season.class);
                    season.setId(documentSnapshot.getId());
                    modelsDataSet.add(season);

                }
                if (allSeasons) {
                    Season all = new Season().allSeason();
                    modelsDataSet.add(0, all);
                }
                if (otherSeason) {
                    Season other = new Season().otherSeason();
                    modelsDataSet.add(other);
                }
                Log.d(TAG, "Automaticky na??ten seznam sezon z db ");
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
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba p??i na????t??n?? pokut z db", error);
                    return;
                }
                modelsDataSet.clear();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Fine fine = documentSnapshot.toObject(Fine.class);
                    fine.setId(documentSnapshot.getId());
                    modelsDataSet.add(fine);

                }

                Log.d(TAG, "Automaticky na??ten seznam pokut z db ");
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
                    Log.e(TAG, "onGetPlayersEvent: nastala chyba p??i na????t??n?? hr?????? z db", error);
                    return;
                }
                modelsDataSet.clear();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Player player = documentSnapshot.toObject(Player.class);
                    player.setId(documentSnapshot.getId());
                    modelsDataSet.add(player);

                }
                Log.d(TAG, "Automaticky na??ten seznam hr?????? z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.PLAYER);
            }
        });
    }

    public void loadMatchesFromRepository() {
        modelsDataSet = new ArrayList<>();
        final CollectionReference collectionReference = db.collection(MATCH_TABLE);
        matchesListener = collectionReference.orderBy("dateOfMatch", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "onGetMatchesEvent: nastala chyba p??i na????t??n?? z??pas?? z db", error);
                    return;
                }
                modelsDataSet.clear();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Match match = documentSnapshot.toObject(Match.class);
                    match.setId(documentSnapshot.getId());
                    modelsDataSet.add(match);

                }
                Log.d(TAG, "Automaticky na??ten seznam z??pas?? z db ");
                changeListener.itemListLoaded(modelsDataSet, Flag.MATCH);
            }
        });
    }

    public void loadMatchFromRepository(String id) {
        DocumentReference document = db.collection(MATCH_TABLE).document(id);
        matchListener = document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, "Listen failed.", error);
                    return;
                }

                if (value != null && value.exists()) {
                    Match match = value.toObject(Match.class);
                    match.setId(value.getId());
                    modelLoadedListener.itemLoaded(match);
                } else {
                    Log.w(TAG, "Current data: null");
                }

            }
        });
    }

        public void loadUsersFromRepository () {
            modelsDataSet = new ArrayList<>();
            CollectionReference collectionReference = db.collection(USER_TABLE);
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, "onGetMatchesEvent: nastala chyba p??i na????t??n?? u??ivatel?? z db", error);
                        return;
                    }
                    modelsDataSet.clear();
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        User user = documentSnapshot.toObject(User.class);
                        user.setId(documentSnapshot.getId());
                        modelsDataSet.add(user);

                    }
                    Log.d(TAG, "Automaticky na??ten seznam u??ivatel?? z db ");
                    changeListener.itemListLoaded(modelsDataSet, Flag.USER);

                }
            });
        }

        public void loadNotificationsFromRepository () {
            modelsDataSet = new ArrayList<>();
            CollectionReference crNotification = db.collection(NOTIFICATION_TABLE);
            crNotification.orderBy("timestamp", Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, "onGetPlayersEvent: nastala chyba p??i na????t??n?? notifikac?? z db", error);
                        return;
                    }
                    modelsDataSet.clear();
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Notification notification = documentSnapshot.toObject(Notification.class);
                        notification.setId(documentSnapshot.getId());
                        modelsDataSet.add(notification);

                    }
                    Log.d(TAG, "Automaticky na??ten seznam notifikaci z db ");
                    notificationListener.notificationListLoaded(modelsDataSet);

                }
            });
        }

        public void loadPkflUrlFromRepository () {
            CollectionReference collectionReference = db.collection(PKFL_TABLE);
            collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG, "onGetMatchesEvent: nastala chyba p??i na????t??n?? pkfl v??c?? z db", error);
                        return;
                    }
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        if (documentSnapshot.getId().equals("url")) {
                            String url = (String) documentSnapshot.get("URL");
                            itemLoadedListener.itemLoaded(url);
                            break;
                        }
                    }
                }
            });
        }

        public void addNotification ( final Notification notification){
            Log.d(TAG, "addNotification: " + notification);
            CollectionReference crNotification = db.collection(NOTIFICATION_TABLE);
            DocumentReference newPlayerRef = crNotification.document();

            newPlayerRef.set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "onComplete: " + task.isSuccessful());
                    if (task.isSuccessful()) {
                    } else {
                        changeListener.alertSent("Chyba p??i p??id??n?? notifikace " + notification + " do db");
                    }
                }
            });
        }
    }
