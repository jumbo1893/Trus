package com.jumbo.trus.update;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jumbo.trus.BuildConfig;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class StorageManager {

    FirebaseStorage storage;
    StorageReference storageRef;
    Context context;
    private static final String TAG = "StorageManager";

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
                //downloadUpdate(url);
               //installAPK();
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
        request.setDescription("updating");
        request.setTitle("My app");
        request.setMimeType("application/vnd.android.package-archive");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
        downloadManager.getUriForDownloadedFile(-1);
    }

    private void installApk(String fileName, String fileExtension, String destinationDirectory) {
        File mFile;
        mFile = new File(Environment.getExternalStorageDirectory() + destinationDirectory+"/" + fileName + fileExtension);
        if (mFile.exists()){
            Intent mIntent = new Intent(Intent.ACTION_VIEW);
            mIntent.setDataAndType(Uri.parse("file://"+mFile.getAbsolutePath()),
                    "application/vnd.android.package-archive");
            context.startActivity(mIntent);
        }else {
            Log.d(TAG,"the file is not exist");
        }

    }

    public void downloadUpdate(String url) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "trus.apk";
        destination += fileName;
        //File file = new File(context.getFilesDir(), "trus.apk");
        final Uri uri = Uri.parse("file://" + destination);
        //Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);

        File file = new File(destination);
        if (file.exists())
            file.delete();

        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setDestinationUri(uri);
        dm.enqueue(request);

        final String finalDestination = destination;
        final BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Log.d(TAG, "onReceive: ");
                //File apk = new File(context.getFilesDir(), "trus.apk");
                //Uri contentUri = FileProvider.getUriForFile(ctxt, BuildConfig.APPLICATION_ID + ".fileprovider", apk);
                Uri contentUri = FileProvider.getUriForFile(ctxt, BuildConfig.APPLICATION_ID + ".fileprovider", new File(finalDestination));
                Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                openFileIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                openFileIntent.setData(contentUri);
                context.startActivity(openFileIntent);
                context.unregisterReceiver(this);
                //finish();
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    void installAPK(){
        String PATH = Environment.getExternalStorageDirectory() + "/" + "trus.apk";
        File file = new File(PATH);
        if(file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriFromFile(context.getApplicationContext(), new File(PATH)), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.getApplicationContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Log.e("TAG", "Error in opening the file!");
            }
        }else{
            Toast.makeText(context.getApplicationContext(),"installing",Toast.LENGTH_LONG).show();
        }
    }
    Uri uriFromFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }
}

