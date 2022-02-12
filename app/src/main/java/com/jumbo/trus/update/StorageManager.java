package com.jumbo.trus.update;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class StorageManager {

    FirebaseStorage storage;
    StorageReference storageRef;
    Context context;

    private DownloadManager downloadManager=null;
    private String filename;



    public StorageManager(Context context, String filename) {
        this.context = context;
        this.filename = filename;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void downloadNewApp() {
        StorageReference storageReference = storageRef.child(filename);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(context, "trus", ".apk", DIRECTORY_DOWNLOADS, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void downloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
        downloadManager.getUriForDownloadedFile(-1);

    }
}

