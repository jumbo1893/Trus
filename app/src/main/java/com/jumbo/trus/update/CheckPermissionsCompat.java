package com.jumbo.trus.update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class CheckPermissionsCompat extends AppCompatActivity {

    public boolean checkSelfPermissionCompat(String permission, int packagePermission) {
        return ActivityCompat.checkSelfPermission(this, permission) == packagePermission;
    }

    public boolean shouldShowRequestPermissionRationaleCompat(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }

    public void requestPermissionsCompat(String[] permissionsArray, int requestCode) {
        if (permissionsArray == null || permissionsArray.length == 0) {
            return;
        }
        ActivityCompat.requestPermissions(this, permissionsArray, requestCode);
    }
}
