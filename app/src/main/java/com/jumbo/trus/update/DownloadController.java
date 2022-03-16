package com.jumbo.trus.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
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

public final class DownloadController {
    private final Context context;
    private final String filename;
    private static final String FILE_NAME = "trus.apk";
    private static final String FILE_BASE_PATH = "file://";
    private static final String MIME_TYPE = "application/vnd.android.package-archive";
    private static final String PROVIDER_PATH = ".provider";
    private static final String APP_INSTALL_PATH = "\"application/vnd.android.package-archive\"";
    private static final String TAG = "DownloadController";

    FirebaseStorage storage;
    StorageReference storageRef;

    public DownloadController(String filename, Context context) {
        this.filename = filename;
        this.context = context;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    public final void enqueueDownload() {
        StorageReference storageReference = storageRef.child(filename);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                startDownload(url);
                saveTextToClipboard("url", url);
                //installAPK();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void startDownload(String url) {
        Log.d(TAG, "enqueueDownload: ");
        String destination = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/";
        destination += FILE_NAME;
        Uri uri = Uri.parse(FILE_BASE_PATH + destination);
        File file = new File(destination);
        if (file.exists()) {
            file.delete();
        }

        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "trus.apk");
        request.setMimeType(MIME_TYPE);
        // set destination
        request.setDestinationUri(uri);
        showInstallOption(destination, uri);
        // Enqueue a new download and same the referenceId
        dm.enqueue(request);
        Toast.makeText(context, "Stahuji...", Toast.LENGTH_LONG).show();
    }

    private final void showInstallOption(final String destination, final Uri uri) {
        final BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Log.d(TAG, "onReceive: ");
                Uri contentUri = FileProvider.getUriForFile(ctxt, BuildConfig.APPLICATION_ID + PROVIDER_PATH, new File(destination));
                Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                openFileIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                openFileIntent.setData(contentUri);
                context.startActivity(openFileIntent);context.unregisterReceiver(this);
                //finish();
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void saveTextToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }
}
